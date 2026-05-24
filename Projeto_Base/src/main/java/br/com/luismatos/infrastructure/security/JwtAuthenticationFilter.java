package br.com.luismatos.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT para rotas /api/**.
 *
 * Não é @Component — gerenciado exclusivamente pelo SecurityConfig da chain de API,
 * o que evita que seja registrado automaticamente no filtro web (Thymeleaf).
 *
 * Fluxo:
 *   1. Extrai "Bearer <token>" do header Authorization
 *   2. Valida o token via JwtService
 *   3. Se válido, autentica o usuário no SecurityContextHolder com a role do token
 *   4. Se inválido/ausente, segue sem autenticar → Spring Security retorna 401
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.tokenValido(token)) {
                String email = jwtService.extrairEmail(token);
                String role  = jwtService.extrairRole(token);

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        email, null,
                        List.of(new SimpleGrantedAuthority(role))
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
