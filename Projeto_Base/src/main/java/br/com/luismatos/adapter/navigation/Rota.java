package br.com.luismatos.adapter.navigation;

/**
 * Registro central de todas as rotas da aplicação.
 *
 * Para adicionar uma nova página:
 *   1. Adicione uma constante aqui com o caminho XHTML e o nível de acesso.
 *   2. Adicione o método irParaXxx() em NavigationController.
 *   3. Adicione o item de menu em layout/topmenu.xhtml.
 *
 * O caminho deve refletir exatamente o arquivo .xhtml em src/main/webapp.
 */
public enum Rota {

    // ── Páginas públicas ──────────────────────────────────────────────────────

    LOGIN("/index.xhtml", TipoAcesso.PUBLICO),

    // ── Requer autenticação ───────────────────────────────────────────────────

    PRINCIPAL("/aplicacao/principal.xhtml", TipoAcesso.AUTENTICADO),

    // ── Requer administrador ──────────────────────────────────────────────────

    NOVO_USUARIO    ("/aplicacao/sistema/novoUsuario.xhtml",     TipoAcesso.ADMINISTRADOR),
    LISTAR_USUARIOS ("/aplicacao/sistema/listarUsuarios.xhtml",  TipoAcesso.ADMINISTRADOR),

    // ── Futuras páginas (crie o .xhtml e descomente o método em NavigationController)

    CADASTRAR_ALUNO    ("/aplicacao/aluno/cadastrar.xhtml",     TipoAcesso.AUTENTICADO),
    LISTAR_ALUNOS      ("/aplicacao/aluno/listar.xhtml",        TipoAcesso.AUTENTICADO),
    CADASTRAR_CURSO    ("/aplicacao/curso/cadastrar.xhtml",     TipoAcesso.AUTENTICADO),
    LISTAR_CURSOS      ("/aplicacao/curso/listar.xhtml",        TipoAcesso.AUTENTICADO),
    CADASTRAR_PROFESSOR("/aplicacao/professor/cadastrar.xhtml", TipoAcesso.AUTENTICADO),
    LISTAR_PROFESSORES ("/aplicacao/professor/listar.xhtml",    TipoAcesso.AUTENTICADO);

    private final String caminho;
    private final TipoAcesso acesso;

    Rota(String caminho, TipoAcesso acesso) {
        this.caminho = caminho;
        this.acesso  = acesso;
    }

    /** Caminho absoluto do arquivo XHTML relativo ao webapp root. */
    public String getCaminho() { return caminho; }

    /** Nível de acesso exigido para acessar esta rota. */
    public TipoAcesso getAcesso() { return acesso; }
}
