package br.com.luismatos.infrastructure.security;

import br.com.luismatos.adapter.navigation.Routes;
import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    // ══════════════════════════════════════════════════════════════════════════
    //  CHAIN 1 — API REST  (/api/**)
    //  Stateless + JWT. Sem CSRF (clientes não usam cookies).
    //  @Order(1) garante prioridade sobre o web chain.
    // ══════════════════════════════════════════════════════════════════════════

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              JwtService jwtService) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(Routes.Api.AUTH_LOGIN).permitAll()
                .requestMatchers(Routes.Api.USUARIOS + "/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class
            )
            // Retorna JSON 401/403 ao invés de redirecionar para /login
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    res.getWriter().write("{\"erro\":\"Não autenticado. Forneça um token JWT válido.\"}");
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    res.getWriter().write("{\"erro\":\"Acesso negado. Permissão insuficiente.\"}");
                })
            );

        return http.build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHAIN 2 — Web (Thymeleaf / Form Login)
    //  Stateful com sessão HTTP. CSRF habilitado (padrão do Spring Security).
    //  @Order(2) — aplica-se a tudo que não é /api/**.
    // ══════════════════════════════════════════════════════════════════════════

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos e Swagger UI — públicos
                .requestMatchers(
                    "/css/**", "/js/**", "/fonts/**", "/imagens/**", "/vendor/**",
                    "/favicon.ico",
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(Routes.Web.LOGIN, "/error").permitAll()
                // Área de sistema: somente ADMINISTRADOR
                .requestMatchers("/aplicacao/sistema/**").hasRole("ADMINISTRADOR")
                // Demais rotas autenticadas
                .requestMatchers("/aplicacao/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage(Routes.Web.LOGIN)
                .loginProcessingUrl(Routes.Web.LOGIN)
                .defaultSuccessUrl(Routes.Web.PRINCIPAL, true)
                .failureUrl(Routes.Web.LOGIN + "?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(Routes.Web.LOGIN + "?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Beans compartilhados pelas duas chains
    // ══════════════════════════════════════════════════════════════════════════

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> {
            Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

            String role = "ROLE_" + usuario.getTipoUsuario().name();
            return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(new SimpleGrantedAuthority(role))
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(Sha256PasswordEncoder sha256) {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return sha256.encode(rawPassword.toString());
            }
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encode(rawPassword).equalsIgnoreCase(encodedPassword);
            }
        };
    }

    // ── CORS — libera origens dos frameworks de frontend em desenvolvimento ───

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origens definidas no application-{profile}.properties
        config.setAllowedOrigins(
            Arrays.asList(allowedOrigins.split(","))
        );
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
