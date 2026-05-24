package br.com.luismatos.adapter.navigation;

public enum TipoAcesso {
    /** Qualquer visitante, sem necessidade de autenticação */
    PUBLICO,

    /** Usuário autenticado (qualquer tipo) */
    AUTENTICADO,

    /** Apenas usuários do tipo ADMINISTRADOR */
    ADMINISTRADOR
}
