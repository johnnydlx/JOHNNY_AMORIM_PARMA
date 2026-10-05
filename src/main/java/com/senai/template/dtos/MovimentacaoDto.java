package com.senai.template.dtos;

import java.time.LocalDateTime;

public class MovimentacaoDto {

    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Long usuarioId;
    private String usuarioNome;
    private String tipo;
    private Integer quantidade;
    private Integer estoqueAnterior;
    private Integer estoqueAtual;
    private LocalDateTime data;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Integer getEstoqueAnterior() { return estoqueAnterior; }
    public void setEstoqueAnterior(Integer estoqueAnterior) { this.estoqueAnterior = estoqueAnterior; }
    public Integer getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(Integer estoqueAtual) { this.estoqueAtual = estoqueAtual; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
