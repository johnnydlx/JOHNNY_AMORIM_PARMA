package com.senai.template.security;

import com.senai.template.entities.UsuarioEntity;
import com.senai.template.repositories.UsuarioRepository;
import com.senai.template.sessoes.SessaoDto;
import com.senai.template.sessoes.SessaoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SincronizarSessaoInterceptor implements HandlerInterceptor {

    private final UsuarioRepository usuarioRepository;

    public SincronizarSessaoInterceptor(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = (Authentication) request.getUserPrincipal();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                && "anonymousUser".equals(authentication.getPrincipal()))) {

            String email = authentication.getName();
            UsuarioEntity usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario != null) {
                HttpSession session = request.getSession();
                SessaoDto sessao = new SessaoDto();
                sessao.setUsuarioId(usuario.getId());
                sessao.setUsuarioNome(usuario.getNome());
                sessao.setUserRole(usuario.getUserRole());
                SessaoUtil.RegistrarSessao(session, sessao);
            }
        }

        return true;
    }
}
