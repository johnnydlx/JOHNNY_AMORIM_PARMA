package com.senai.template.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioDto {

    private Long id;

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    private String email;

    @NotBlank(message = "A senha e obrigatoria")
    private String senha;

    private Integer userRole = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }
}

