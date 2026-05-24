package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.navigation.Rota;
import br.com.luismatos.adapter.navigation.TipoAcesso;
import br.com.luismatos.adapter.shared.FacesUtil;
import br.com.luismatos.adapter.shared.SessionContext;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * Controlador central de navegação da aplicação.
 *
 * Como adicionar uma nova página:
 *   1. Adicione a constante em Rota.java com o caminho e o nível de acesso.
 *   2. Adicione o método irParaXxx() aqui (uma linha).
 *   3. Adicione o item de menu em layout/topmenu.xhtml.
 */
@ManagedBean
@SessionScoped
public class NavigationController implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Páginas públicas ──────────────────────────────────────────────────────

    public String irParaLogin() { return navegar(Rota.LOGIN); }

    // ── Área autenticada ──────────────────────────────────────────────────────

    public String irParaPrincipal()          { return navegar(Rota.PRINCIPAL); }

    public String irParaCadastrarAluno()     { return navegar(Rota.CADASTRAR_ALUNO); }
    public String irParaListarAlunos()       { return navegar(Rota.LISTAR_ALUNOS); }

    public String irParaCadastrarCurso()     { return navegar(Rota.CADASTRAR_CURSO); }
    public String irParaListarCursos()       { return navegar(Rota.LISTAR_CURSOS); }

    public String irParaCadastrarProfessor() { return navegar(Rota.CADASTRAR_PROFESSOR); }
    public String irParaListarProfessores()  { return navegar(Rota.LISTAR_PROFESSORES); }

    // ── Área de administrador ─────────────────────────────────────────────────

    public String irParaNovoUsuario()        { return navegar(Rota.NOVO_USUARIO); }
    public String irParaListarUsuarios()     { return navegar(Rota.LISTAR_USUARIOS); }

    // ── Keep-alive de sessão ──────────────────────────────────────────────────

    /**
     * Chamado via AJAX pelo sessionTimer.js quando o usuário clica em "Continuar".
     * Não precisa de lógica: qualquer request ao servidor renova o timeout da HttpSession.
     */
    public String keepAlive() {
        return null; // permanece na mesma página
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public String doLogout() {
        SessionContext.getInstance().encerrarSessao();
        return Rota.LOGIN.getCaminho() + "?faces-redirect=true";
    }

    // ── Controle de visibilidade (uso em rendered= no XHTML) ─────────────────

    /**
     * Verifica se o usuário logado pode acessar a rota pelo nome da constante.
     *
     * Exemplo no XHTML:
     *   rendered="#{navigationController.podeAcessar('NOVO_USUARIO')}"
     */
    public boolean podeAcessar(String nomeRota) {
        try {
            return temAcesso(Rota.valueOf(nomeRota));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ── Lógica interna ────────────────────────────────────────────────────────

    /**
     * Verifica o acesso e retorna o outcome JSF.
     * ?faces-redirect=true instrui o JSF a fazer um redirect real (302),
     * o que faz o browser exibir o caminho .xhtml correto na barra de endereços.
     */
    private String navegar(Rota rota) {
        if (!temAcesso(rota)) {
            FacesUtil.addErrorMessage("Você não tem permissão para acessar esta página.");
            return null;
        }
        return rota.getCaminho() + "?faces-redirect=true";
    }

    private boolean temAcesso(Rota rota) {
        Usuario usuarioLogado = getUsuarioLogado();
        switch (rota.getAcesso()) {
            case PUBLICO:
                return true;
            case AUTENTICADO:
                return usuarioLogado != null;
            case ADMINISTRADOR:
                return usuarioLogado != null
                    && TipoUsuario.ADMINISTRADOR.equals(usuarioLogado.getTipoUsuario());
            default:
                return false;
        }
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SessionContext.getInstance().getAttribute("info_user");
    }
}
