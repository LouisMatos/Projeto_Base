package br.com.luismatos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.luismatos.util.SecurityUtil;



@Entity
@Table(name = "tb01_usuario")
public class Usuario implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8394679655534159556L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "SENHA")
	private String senha;
	
	@Column(name = "TIPOUSUARIO")
	private TipoUsuario tipoUsuario;
	
	@Column(name = "CONFIRMASENHA")
	private String confirmaSenha;
	
	public Usuario(){
		
	}
	
	

	public Usuario(long id, String email, String senha, TipoUsuario tipoUsuario) {
		super();
		this.id = id;
		this.email = email;
		this.senha = SecurityUtil.getHashPassword(senha);
		this.tipoUsuario = tipoUsuario;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	@SuppressWarnings("null")
	public void setSenha(String senha) {
		
		boolean teste = senha.isEmpty();
				
		if(teste != true){
			this.senha = SecurityUtil.getHashPassword(senha);
		}else{
			this.senha = "";
		}
		
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = SecurityUtil.getHashPassword(senha);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confirmaSenha == null) ? 0 : confirmaSenha.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((senha == null) ? 0 : senha.hashCode());
		result = prime * result + ((tipoUsuario == null) ? 0 : tipoUsuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (confirmaSenha == null) {
			if (other.confirmaSenha != null)
				return false;
		} else if (!confirmaSenha.equals(other.confirmaSenha))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (senha == null) {
			if (other.senha != null)
				return false;
		} else if (!senha.equals(other.senha))
			return false;
		if (tipoUsuario != other.tipoUsuario)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", email=" + email + ", senha=" + senha + ", tipoUsuario=" + tipoUsuario
				+ ", confirmaSenha=" + confirmaSenha + "]";
	}
	
	
	
	
	
	
}
