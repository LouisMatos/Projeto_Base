package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.shared.SessionContext;
import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class UsuarioLogadoController implements Serializable {

    private static final long serialVersionUID = 5876307468675945587L;

    public Usuario getUsuario() {
        return SessionContext.getInstance().getUsuarioLogado();
    }

    public boolean isLogado() {
        return getUsuario() != null;
    }

    public boolean isAdministrador() {
        Usuario usuario = getUsuario();
        return usuario != null && TipoUsuario.ADMINISTRADOR.equals(usuario.getTipoUsuario());
    }
}
