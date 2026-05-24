package br.com.luismatos.adapter.api.dto;

import br.com.luismatos.domain.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    // Senha é opcional em PUT (atualização) — validada no controller
    String senha,

    String confirmaSenha,

    @NotNull(message = "Tipo de usuário é obrigatório")
    TipoUsuario tipoUsuario
) {}
