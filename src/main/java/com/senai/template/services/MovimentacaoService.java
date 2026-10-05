package com.senai.template.services;

import com.senai.template.dtos.MovimentacaoDto;
import com.senai.template.entities.MovimentacaoEntity;
import com.senai.template.entities.ProdutoEntity;
import com.senai.template.entities.UsuarioEntity;
import com.senai.template.repositories.MovimentacaoRepository;
import com.senai.template.repositories.ProdutoRepository;
import com.senai.template.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository,
                               UsuarioRepository usuarioRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MovimentacaoDto> listarTodos() {
        List<MovimentacaoDto> movimentacoes = new ArrayList<>();

        for (MovimentacaoEntity entity : movimentacaoRepository.findAllByOrderByDataDesc()) {
            movimentacoes.add(converterEntityParaDto(entity));
        }

        return movimentacoes;
    }

    public List<MovimentacaoDto> listarPorProduto(Long produtoId) {
        List<MovimentacaoDto> movimentacoes = new ArrayList<>();

        for (MovimentacaoEntity entity : movimentacaoRepository.findByProdutoIdOrderByDataDesc(produtoId)) {
            movimentacoes.add(converterEntityParaDto(entity));
        }

        return movimentacoes;
    }

    @Transactional
    public String registrar(Long produtoId, String tipo, Integer quantidade, Long usuarioId) {
        if (produtoId == null || tipo == null || quantidade == null || usuarioId == null) {
            return "Produto, tipo, quantidade e usuário são obrigatórios.";
        }

        if (!tipo.equals("ENTRADA") && !tipo.equals("SAIDA")) {
            return "Tipo de movimentação inválido.";
        }

        if (quantidade <= 0) {
            return "A quantidade deve ser maior que zero.";
        }

        Optional<ProdutoEntity> produtoEncontrado = produtoRepository.findById(produtoId);
        if (produtoEncontrado.isEmpty()) {
            return "Produto não encontrado.";
        }

        Optional<UsuarioEntity> usuarioEncontrado = usuarioRepository.findById(usuarioId);
        if (usuarioEncontrado.isEmpty()) {
            return "Usuário não encontrado.";
        }

        ProdutoEntity produto = produtoEncontrado.get();

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            return "Não é possível movimentar um produto inativo.";
        }

        Integer estoqueAnterior = produto.getEstoque() == null ? 0 : produto.getEstoque();
        Integer estoqueAtual;

        if (tipo.equals("ENTRADA")) {
            estoqueAtual = estoqueAnterior + quantidade;
        } else {
            if (quantidade > estoqueAnterior) {
                return "Saída não permitida: estoque insuficiente. Disponível: " + estoqueAnterior + ". Solicitado: " + quantidade + ".";
            }

            estoqueAtual = estoqueAnterior - quantidade;
        }

        produto.setEstoque(estoqueAtual);
        produtoRepository.save(produto);

        MovimentacaoEntity movimentacao = new MovimentacaoEntity();
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuarioEncontrado.get());
        movimentacao.setTipo(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setEstoqueAnterior(estoqueAnterior);
        movimentacao.setEstoqueAtual(estoqueAtual);
        movimentacao.setData(LocalDateTime.now());

        movimentacaoRepository.save(movimentacao);

        return null;
    }

    public List<ProdutoEntity> listarEstoqueOrdenado() {
        List<ProdutoEntity> produtos = new ArrayList<>(produtoRepository.findAll());

        for (int i = 0; i < produtos.size() - 1; i++) {
            for (int j = 0; j < produtos.size() - i - 1; j++) {
                Integer estoqueAtual = produtos.get(j).getEstoque();
                Integer proximoEstoque = produtos.get(j + 1).getEstoque();

                if (estoqueAtual > proximoEstoque) {
                    ProdutoEntity aux = produtos.get(j);
                    produtos.set(j, produtos.get(j + 1));
                    produtos.set(j + 1, aux);
                }
            }
        }

        return produtos;
    }

    public List<ProdutoEntity> listarBaixoEstoque() {
        List<ProdutoEntity> produtos = new ArrayList<>();

        for (ProdutoEntity produto : listarEstoqueOrdenado()) {
            Integer estoque = produto.getEstoque() == null ? 0 : produto.getEstoque();
            Integer minimo = produto.getEstoqueMinimo() == null ? 0 : produto.getEstoqueMinimo();

            if (Boolean.TRUE.equals(produto.getAtivo()) && estoque <= minimo) {
                produtos.add(produto);
            }
        }

        return produtos;
    }

    private MovimentacaoDto converterEntityParaDto(MovimentacaoEntity entity) {
        MovimentacaoDto dto = new MovimentacaoDto();

        dto.setId(entity.getId());
        dto.setProdutoId(entity.getProduto().getId());
        dto.setProdutoNome(entity.getProduto().getNome());
        dto.setUsuarioId(entity.getUsuario().getId());
        dto.setUsuarioNome(entity.getUsuario().getNome());
        dto.setTipo(entity.getTipo());
        dto.setQuantidade(entity.getQuantidade());
        dto.setEstoqueAnterior(entity.getEstoqueAnterior());
        dto.setEstoqueAtual(entity.getEstoqueAtual());
        dto.setData(entity.getData());

        return dto;
    }
}
