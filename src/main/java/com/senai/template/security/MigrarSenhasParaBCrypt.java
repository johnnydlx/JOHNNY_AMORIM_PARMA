package com.senai.template.security;

import com.senai.template.entities.UsuarioEntity;
import com.senai.template.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MigrarSenhasParaBCrypt implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public MigrarSenhasParaBCrypt(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (UsuarioEntity usuario : usuarioRepository.findAll()) {
            String senha = usuario.getSenha();
            if (senha != null && !senha.isBlank() && !senha.startsWith("$2a$")
                    && !senha.startsWith("$2b$") && !senha.startsWith("$2y$")) {
                usuario.setSenha(passwordEncoder.encode(senha));
                usuarioRepository.save(usuario);
            }
        }
    }
}
