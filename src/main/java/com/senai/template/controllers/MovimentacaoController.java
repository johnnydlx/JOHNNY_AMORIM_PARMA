package com.senai.template.controllers;

import com.senai.template.services.MovimentacaoService;
import com.senai.template.sessoes.SessaoDto;
import com.senai.template.sessoes.SessaoUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping("/movimentacoes/salvar")
    public String salvar(@RequestParam Long produtoId,
                         @RequestParam String tipo,
                         @RequestParam Integer quantidade,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        SessaoDto usuario = SessaoUtil.ObterSessao(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        String erro = movimentacaoService.registrar(
                produtoId,
                tipo,
                quantidade,
                usuario.getUsuarioId()
        );

        if (erro != null) {
            redirectAttributes.addFlashAttribute("erroMovimentacao", erro);
        } else {
            redirectAttributes.addFlashAttribute("mensagemMovimentacao", "Movimentação registrada com sucesso.");
        }

        return "redirect:/estoque";
    }
}
