package br.com.luismatos.adapter.shared;

import br.com.luismatos.domain.model.Usuario;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class SessionContext {

    /** Chave usada para armazenar o usuário autenticado na sessão HTTP. */
    public static final String CHAVE_USUARIO = "usuario_logado";

    // Initialization-on-demand holder: thread-safe sem synchronized em cada chamada
    private static class Holder {
        private static final SessionContext INSTANCE = new SessionContext();
    }

    public static SessionContext getInstance() {
        return Holder.INSTANCE;
    }

    private SessionContext() {}

    private ExternalContext externalContext() {
        if (FacesContext.getCurrentInstance() == null) {
            throw new IllegalStateException(
                "SessionContext não pode ser chamado fora de uma requisição JSF");
        }
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public void encerrarSessao() {
        externalContext().invalidateSession();
    }

    public Object getAttribute(String nome) {
        return externalContext().getSessionMap().get(nome);
    }

    public void setAttribute(String nome, Object valor) {
        externalContext().getSessionMap().put(nome, valor);
    }

    /** Retorna o usuário autenticado na sessão, ou {@code null} se não houver sessão ativa. */
    public Usuario getUsuarioLogado() {
        return (Usuario) getAttribute(CHAVE_USUARIO);
    }

    /** Armazena o usuário autenticado na sessão corrente. */
    public void setUsuarioLogado(Usuario usuario) {
        setAttribute(CHAVE_USUARIO, usuario);
    }
}
