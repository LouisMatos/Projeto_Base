package br.com.luismatos.adapter.api;

import br.com.luismatos.adapter.navigation.Routes;
import br.com.luismatos.adapter.api.dto.UsuarioRequest;
import br.com.luismatos.adapter.api.dto.UsuarioResponse;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.domain.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Usuários", description = "Gestão de usuários — requer role ADMINISTRADOR")
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping(Routes.Api.USUARIOS)
    @Operation(summary = "Listar todos os usuários")
    public List<UsuarioResponse> listar() {
        return usuarioService.listarTodos().stream()
            .map(UsuarioResponse::de)
            .toList();
    }

    @PostMapping(Routes.Api.USUARIOS)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criar novo usuário",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
        }
    )
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        validarSenhas(request);
        return UsuarioResponse.de(usuarioService.registrar(toEntity(request)));
    }

    @PutMapping(Routes.Api.USUARIO_BY_ID)
    @Operation(
        summary = "Atualizar usuário existente",
        description = "Senha: se omitida ou vazia, mantém a senha atual.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public UsuarioResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody UsuarioRequest request) {
        if (senhaInformada(request)) {
            validarSenhas(request);
        }
        Usuario usuario = toEntity(request);
        usuario.setId(id);
        return UsuarioResponse.de(usuarioService.atualizar(usuario));
    }

    @DeleteMapping(Routes.Api.USUARIO_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Excluir usuário",
        responses = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validarSenhas(UsuarioRequest req) {
        if (req.senha() == null || req.senha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (!req.senha().equals(req.confirmaSenha())) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }
    }

    private boolean senhaInformada(UsuarioRequest req) {
        return req.senha() != null && !req.senha().isBlank();
    }

    private Usuario toEntity(UsuarioRequest req) {
        Usuario u = new Usuario();
        u.setEmail(req.email());
        u.setSenha(req.senha());
        u.setConfirmaSenha(req.confirmaSenha());
        u.setTipoUsuario(req.tipoUsuario());
        return u;
    }
}
