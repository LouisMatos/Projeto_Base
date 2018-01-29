package br.com.luismatos.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class TopmenuController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4296968999226392269L;


	public void redirecionaPaginaNovoUsuario() throws IOException{
		FacesContext.getCurrentInstance().getExternalContext().redirect("novoUsuario");
	}

}
