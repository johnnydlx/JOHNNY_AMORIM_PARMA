package com.senai.template.sessoes;

import jakarta.servlet.http.HttpSession;

public final class SessaoUtil {

    //--Nome da variável que vamos usar para armazenar os dados da sessão
    private static final String USUARIO_LOGADO = "usuarioLogado";

    private SessaoUtil() {
    }

    //-- Método para registar o usuário nome e id na sessão
    public static void RegistrarSessao(HttpSession session, SessaoDto sessaoDto) {
        session.setAttribute(USUARIO_LOGADO, sessaoDto);
    }

    //-- Método para obter o usuário nome e id na sessão
    public static SessaoDto ObterSessao(HttpSession session) {
        Object usuarioLogado = session.getAttribute(USUARIO_LOGADO);

        if (usuarioLogado == null) {
            return null;
        }

        //--Cast explicito para converter Object em SessaoDto
        return (SessaoDto) usuarioLogado;
    }

    public static void RemoverSessao(HttpSession session) {
        session.removeAttribute(USUARIO_LOGADO);
        session.invalidate();
    }
}
