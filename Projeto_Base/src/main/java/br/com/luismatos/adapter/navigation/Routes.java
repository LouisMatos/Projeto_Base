package br.com.luismatos.adapter.navigation;

/**
 * Ponto único de definição de rotas da aplicação.
 * Todas as URLs — web e API — são declaradas aqui como constantes,
 * permitindo alteração centralizada sem tocar nos controllers.
 */
public final class Routes {

    private Routes() {}

    // ── Web (Thymeleaf / Form Login) ─────────────────────────────────────────

    public static final class Web {
        private Web() {}

        public static final String INDEX              = "/";
        public static final String LOGIN              = "/login";
        public static final String PRINCIPAL          = "/aplicacao/principal";

        public static final String USUARIO_LISTAR     = "/aplicacao/sistema/usuario/listar";
        public static final String USUARIO_NOVO       = "/aplicacao/sistema/usuario/novo";
        public static final String USUARIO_EDITAR     = "/aplicacao/sistema/usuario/editar";
        public static final String USUARIO_EXCLUIR_ID = "/aplicacao/sistema/usuario/excluir/{id}";
    }

    // ── REST API ─────────────────────────────────────────────────────────────

    public static final class Api {
        private Api() {}

        public static final String AUTH_LOGIN    = "/api/v1/auth/login";
        public static final String AUTH_ME       = "/api/v1/auth/me";

        public static final String USUARIOS      = "/api/v1/usuarios";
        public static final String USUARIO_BY_ID = "/api/v1/usuarios/{id}";
    }

    // ── View names (templates Thymeleaf) ─────────────────────────────────────

    public static final class Views {
        private Views() {}

        public static final String LOGIN           = "login";
        public static final String PRINCIPAL       = "aplicacao/principal";
        public static final String LISTAR_USUARIOS = "aplicacao/sistema/listarUsuarios";
        public static final String NOVO_USUARIO    = "aplicacao/sistema/novoUsuario";
    }
}
