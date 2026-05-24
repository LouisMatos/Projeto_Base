package br.com.luismatos.domain.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tb01_usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 8394679655534159556L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "SENHA")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOUSUARIO")
    private TipoUsuario tipoUsuario;

    // Campo apenas para validação de formulário — não persiste no banco
    @Transient
    private String confirmaSenha;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getConfirmaSenha() { return confirmaSenha; }
    public void setConfirmaSenha(String confirmaSenha) { this.confirmaSenha = confirmaSenha; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario)) return false;
        Usuario other = (Usuario) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", email=" + email + ", tipoUsuario=" + tipoUsuario + "}";
    }
}
