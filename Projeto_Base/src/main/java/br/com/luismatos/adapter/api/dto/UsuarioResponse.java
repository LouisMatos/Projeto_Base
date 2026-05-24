package br.com.luismatos.adapter.api.dto;

import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;

public record UsuarioResponse(
    Long       id,
    String     email,
    TipoUsuario tipoUsuario
) {
    // Factory method — converte entidade → DTO sem expor a entidade JPA
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getTipoUsuario());
    }
}
