package br.com.luismatos.adapter.shared;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class SessionContext {

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
            throw new IllegalStateException("SessionContext não pode ser chamado fora de uma requisição JSF");
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
}
