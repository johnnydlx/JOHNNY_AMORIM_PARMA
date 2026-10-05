package com.senai.template.controllers;

import com.senai.template.dtos.CategoriaDto;
import com.senai.template.services.CategoriaService;
import com.senai.template.sessoes.SessaoDto;
import com.senai.template.sessoes.SessaoUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping("/categorias/salvar")
    public String salvar(@ModelAttribute CategoriaDto categoriaDto,
                         RedirectAttributes redirectAttributes,
                         HttpSession session) {

        SessaoDto usuario = SessaoUtil.ObterSessao(session);
        if (usuario == null) {
            return "redirect:/login";
        }
        if (categoriaDto.getNome() == null || categoriaDto.getNome().isBlank()) {
            redirectAttributes.addFlashAttribute("erroCategoria", "O nome da categoria e obrigatorio.");
            return "redirect:/categorias";
        }

        if (categoriaDto.getId() == null) {
            categoriaService.cadastrarCategoria(categoriaDto);
        } else {
            CategoriaDto atualizada = categoriaService.atualizarCategoria(categoriaDto.getId(), categoriaDto);
            if (atualizada == null) {
                redirectAttributes.addFlashAttribute("erroCategoria", "Categoria nao encontrada.");
                return "redirect:/categorias";
            }
        }

        redirectAttributes.addFlashAttribute("mensagemCategoria", "Categoria salva com sucesso.");
        return "redirect:/categorias";
    }

    @DeleteMapping("/categoriaexcluir/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluir(@PathVariable Long id, HttpSession session) {
        SessaoDto usuario = SessaoUtil.ObterSessao(session);
        if (usuario == null || usuario.getUserRole() == null || usuario.getUserRole() != 1) {
            return ResponseEntity.status(403).build();
        }

        boolean excluiu = categoriaService.excluirCategoria(id);

        if (!excluiu) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
