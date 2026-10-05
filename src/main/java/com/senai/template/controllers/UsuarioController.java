package com.senai.template.controllers;

import com.senai.template.dtos.TrocaSenhaDto;
import com.senai.template.dtos.UsuarioDto;
import com.senai.template.services.UsuarioService;
import com.senai.template.sessoes.SessaoDto;
import com.senai.template.sessoes.SessaoUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuarios/salvar")
    public String salvar(@ModelAttribute UsuarioDto usuarioDto,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {

        if (usuarioDto.getNome() == null || usuarioDto.getNome().isBlank()
                || usuarioDto.getEmail() == null || usuarioDto.getEmail().isBlank()) {
            redirectAttributes.addFlashAttribute("erroUsuario", "Nome e e-mail sao obrigatorios.");
            return "redirect:/usuarios";
        }

        if (usuarioDto.getId() == null
                && (usuarioDto.getSenha() == null || usuarioDto.getSenha().isBlank())) {
            redirectAttributes.addFlashAttribute("erroUsuario", "A senha e obrigatoria para novo usuario.");
            return "redirect:/usuarios";
        }

        SessaoDto sessao = SessaoUtil.ObterSessao(session);
        if (sessao == null) {
            return "redirect:/login";
        }
        boolean isAdmin = sessao.getUserRole() != null && sessao.getUserRole() == 1;
        if (!isAdmin) {
            if (usuarioDto.getId() != null) {
                UsuarioDto existente = usuarioService.buscarPorId(usuarioDto.getId());
                if (existente == null || (existente.getUserRole() != null && existente.getUserRole() == 1)) {
                    redirectAttributes.addFlashAttribute("erroUsuario", "O operador pode apenas inserir ou atualizar usuários operadores.");
                    return "redirect:/usuarios";
                }
            }
            usuarioDto.setUserRole(0);
        }
        try {
            UsuarioDto salvo = usuarioService.salvar(usuarioDto);

            if (salvo == null) {
                redirectAttributes.addFlashAttribute("erroUsuario", "Usuario nao encontrado.");
                return "redirect:/usuarios";
            }

            redirectAttributes.addFlashAttribute("mensagemUsuario", "Usuario salvo com sucesso.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("erroUsuario", "Ja existe um usuario com este e-mail.");
        }

        return "redirect:/usuarios";
    }

    @DeleteMapping("/usuarioexcluir/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluir(@PathVariable Long id, HttpSession session) {
        SessaoDto sessao = SessaoUtil.ObterSessao(session);
        if (sessao == null || sessao.getUserRole() == null || sessao.getUserRole() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (usuarioService.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.excluir(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuario/senha/{login}")
    @ResponseBody
    public ResponseEntity<String> trocarSenha(@PathVariable String login,
                                               @RequestBody TrocaSenhaDto trocaSenhaDto,
                                               HttpSession session) {

        SessaoDto sessao = SessaoUtil.ObterSessao(session);
        if (sessao == null || sessao.getUserRole() == null || sessao.getUserRole() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas administradores podem alterar senhas.");
        }

        if (trocaSenhaDto.getSenhaAtual() == null || trocaSenhaDto.getSenhaAtual().isBlank()
                || trocaSenhaDto.getSenhaNova() == null || trocaSenhaDto.getSenhaNova().isBlank()
                || trocaSenhaDto.getSenhaNovaConfirmacao() == null || trocaSenhaDto.getSenhaNovaConfirmacao().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Senha atual e nova senha são obrigatórias.");
        }

        if (trocaSenhaDto.getSenhaNova().length() < 8) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao realizar a troca de senha: a nova senha deve ter pelo menos 8 caracteres.");
        }

        if (!trocaSenhaDto.getSenhaNova().equals(trocaSenhaDto.getSenhaNovaConfirmacao())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao realizar a troca de senha: a confirmação da nova senha está incorreta.");
        }

        boolean alterou = usuarioService.trocarSenha(
                login,
                trocaSenhaDto.getSenhaAtual(),
                trocaSenhaDto.getSenhaNova()
        );

        if (!alterou) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao realizar a troca de senha: senha atual incorreta.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Senha alterada com sucesso.");
    }
}
