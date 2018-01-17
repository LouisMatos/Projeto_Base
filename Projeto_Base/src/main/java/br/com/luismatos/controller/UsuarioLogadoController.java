package br.com.luismatos.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.luismatos.model.TipoUsuario;
import br.com.luismatos.model.Usuario;

@ManagedBean
@SessionScoped
public class UsuarioLogadoController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876307468675945587L;
	
	private Usuario usuario;
	
	public void logar(Usuario usuario){
		this.usuario = usuario;
	}
	
	public void deslogar() throws IOException{
		this.usuario = null;
		FacesContext.getCurrentInstance().getExternalContext().redirect("../../index.xhtml");
	}
	
	public boolean isLogdo(){
		return usuario != null;
	}
	
	public boolean isAdministrador(){
		return usuario != null && usuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR;
	}
	

}
