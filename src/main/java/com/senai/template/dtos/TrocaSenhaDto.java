package com.senai.template.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TrocaSenhaDto {

    private String senhaAtual;
    private String senhaNova;

    @JsonAlias("SenhaNovaConfirmacao")
    private String senhaNovaConfirmacao;

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getSenhaNova() {
        return senhaNova;
    }

    public void setSenhaNova(String senhaNova) {
        this.senhaNova = senhaNova;
    }

    public String getSenhaNovaConfirmacao() {
        return senhaNovaConfirmacao;
    }

    public void setSenhaNovaConfirmacao(String senhaNovaConfirmacao) {
        this.senhaNovaConfirmacao = senhaNovaConfirmacao;
    }
}
