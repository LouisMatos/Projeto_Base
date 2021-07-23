package br.com.luismatos.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.luismatos.impl.UsuarioDAOImplementation;
import br.com.luismatos.model.Usuario;
import br.com.luismatos.session.FacesUtil;
import br.com.luismatos.session.SessionContext;

@ManagedBean

public class LoginController implements Serializable {

	private static final long serialVersionUID = 1L;
	private Usuario usuario;
	private UsuarioDAOImplementation dao = new UsuarioDAOImplementation();
	
	
	
	
	public LoginController(){
		usuario = new Usuario();
	}


	public void efetuarLogin() throws IOException {
		
		Usuario user = new Usuario();

		user = dao.existeUsuario(usuario);
		
		System.out.println(usuario.toString());
		
		if(usuario.getSenha() == "" && usuario.getEmail() == ""){
			FacesUtil.assErrorMessage("Informar Usuário!");
			FacesUtil.assErrorMessage("Informar Senha!");
		}else if(usuario.getEmail() == ""){
			FacesUtil.assErrorMessage("Informar Usuário!");
		}else if(usuario.getSenha() == ""){
			FacesUtil.assErrorMessage("Informar Senha!");
		}else{
			if (user != null && user.getEmail().toLowerCase().equals(usuario.getEmail().toLowerCase()) && user.getSenha().equals(usuario.getSenha())) {
				SessionContext.getInstance().setAttribute("info_user", user);
				FacesContext.getCurrentInstance().getExternalContext().redirect("principal");	
			} else {
				FacesUtil.assErrorMessage("Usuário e/ou senha incorreto(s)!");
			}
		}
		
		 
		 
		 
		 
	}
	
	
	public void teste(){
		FacesUtil.addInfoMessage("Teste Daora");
	}

	public boolean isLogdo() {
		return usuario != null;
	}

	public void doEfetuarLogout() throws IOException {
		SessionContext.getInstance().encerrarSessao();

		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		FacesContext.getCurrentInstance().getExternalContext().redirect(context.getRequestContextPath());
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
