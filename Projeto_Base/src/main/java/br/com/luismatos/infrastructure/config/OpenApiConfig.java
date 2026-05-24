package br.com.luismatos.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("Projeto Base — API REST")
                .description("""
                    API REST — Fase 3 da migração Spring Boot 3.

                    **Autenticação:** faça POST em `/api/v1/auth/login` com `{email, senha}` \
                    para receber o token JWT. Use o token no header `Authorization: Bearer <token>`.
                    """)
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList(schemeName))
            .components(new Components()
                .addSecuritySchemes(schemeName,
                    new SecurityScheme()
                        .name(schemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token JWT obtido em POST /api/v1/auth/login")));
    }
}
