package br.com.luismatos.adapter.controller;

import br.com.luismatos.adapter.navigation.Rota;
import br.com.luismatos.adapter.shared.FacesUtil;
import br.com.luismatos.adapter.shared.SessionContext;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.infrastructure.persistence.UsuarioRepositoryImpl;
import br.com.luismatos.infrastructure.security.Sha256PasswordEncoder;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Optional;

@ManagedBean
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario;
    private final UsuarioService usuarioService;

    public LoginController() {
        usuario = new Usuario();
        usuarioService = new UsuarioService(
            new UsuarioRepositoryImpl(),
            new Sha256PasswordEncoder()
        );
    }

    /**
     * Autentica o usuário e retorna o outcome JSF para navegação.
     * Retornar String (em vez de void + redirect manual) permite que o JSF
     * processe a navegação inclusive em contexto AJAX, preservando a URL no browser.
     */
    public String login() {
        if (credenciaisAusentes()) {
            FacesUtil.addErrorMessage("Informe o e-mail e a senha.");
            return null;
        }

        Optional<Usuario> autenticado = usuarioService.autenticar(
            usuario.getEmail(), usuario.getSenha()
        );

        if (autenticado.isPresent()) {
            SessionContext.getInstance().setUsuarioLogado(autenticado.get());
            return Rota.PRINCIPAL.getCaminho() + "?faces-redirect=true";
        }

        FacesUtil.addErrorMessage("E-mail e/ou senha incorretos.");
        return null;
    }

    public boolean isLogado() {
        return SessionContext.getInstance().getUsuarioLogado() != null;
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    private boolean credenciaisAusentes() {
        return usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()
            || usuario.getSenha() == null || usuario.getSenha().trim().isEmpty();
    }
}
