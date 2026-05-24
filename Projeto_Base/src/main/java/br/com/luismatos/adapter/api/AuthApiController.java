package br.com.luismatos.adapter.api;

import br.com.luismatos.adapter.navigation.Routes;
import br.com.luismatos.adapter.api.dto.LoginRequest;
import br.com.luismatos.adapter.api.dto.LoginResponse;
import br.com.luismatos.adapter.api.dto.UsuarioResponse;
import br.com.luismatos.application.UsuarioService;
import br.com.luismatos.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Autenticação", description = "Login e informações do usuário autenticado")
public class AuthApiController {

    private final UsuarioService usuarioService;
    private final JwtService     jwtService;
    private final long           expirationMs;

    public AuthApiController(UsuarioService usuarioService,
                             JwtService jwtService,
                             @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.usuarioService = usuarioService;
        this.jwtService     = jwtService;
        this.expirationMs   = expirationMs;
    }

    @PostMapping(Routes.Api.AUTH_LOGIN)
    @Operation(
        summary = "Autenticar usuário",
        description = "Recebe e-mail e senha, retorna token JWT para uso nos demais endpoints.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        }
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return usuarioService.autenticar(request.email(), request.senha())
            .map(u -> {
                String role  = "ROLE_" + u.getTipoUsuario().name();
                String token = jwtService.gerarToken(u.getEmail(), role);
                return ResponseEntity.ok(
                    new LoginResponse(token, u.getEmail(), u.getTipoUsuario().name(), expirationMs)
                );
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping(Routes.Api.AUTH_ME)
    @Operation(
        summary = "Dados do usuário autenticado",
        description = "Retorna as informações do usuário representado pelo token JWT.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Dados retornados"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado no banco")
        }
    )
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email)
            .map(u -> ResponseEntity.ok(UsuarioResponse.de(u)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
