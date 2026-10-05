package com.senai.template.controllers;

import com.senai.template.dtos.ProdutoDto;
import com.senai.template.services.ProdutoService;
import com.senai.template.sessoes.SessaoDto;
import com.senai.template.sessoes.SessaoUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping("/produtos/salvar")
    public String salvar(@ModelAttribute ProdutoDto produtoDto,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        SessaoDto usuario = SessaoUtil.ObterSessao(session);
        if (usuario == null) return "redirect:/login";
        if (produtoDto.getNome() == null || produtoDto.getNome().isBlank()
                || produtoDto.getPreco() == null || produtoDto.getEstoque() == null || produtoDto.getEstoqueMinimo() == null) {
            redirectAttributes.addFlashAttribute("erroProduto", "Nome, preco, estoque e estoque minimo sao obrigatorios.");
            return "redirect:/produtos";
        }
        if (produtoDto.getPreco().signum() < 0
                || produtoDto.getEstoque() < 0
                || produtoDto.getEstoqueMinimo() < 0) {
            redirectAttributes.addFlashAttribute("erroProduto", "Preço, estoque e estoque mínimo não podem ser negativos.");
            return "redirect:/produtos";
        }
        ProdutoDto salvo = produtoService.salvar(produtoDto, usuario.getUsuarioId());
        if (salvo == null) {
            redirectAttributes.addFlashAttribute("erroProduto", "Nao foi possivel salvar o produto. Confira a categoria.");
        } else {
            redirectAttributes.addFlashAttribute("mensagemProduto", "Produto salvo com sucesso.");
        }
        return "redirect:/produtos";
    }

    @DeleteMapping("/produtoexcluir/{id}")
    @ResponseBody
    public ResponseEntity<Void> excluir(@PathVariable Long id, HttpSession session) {
        SessaoDto usuario = SessaoUtil.ObterSessao(session);
        if (usuario == null || usuario.getUserRole() == null || usuario.getUserRole() != 1) {
            return ResponseEntity.status(403).build();
        }
        if (!produtoService.excluir(id)) {
            if (produtoService.buscarPorId(id) != null) {
                return ResponseEntity.status(409).build();
            }
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
