package br.com.luismatos.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para JwtService.
 * Não necessita de contexto Spring — instanciação direta com valores de teste.
 */
class JwtServiceTest {

    // Chave base64 de 32 bytes (256 bits) — apenas para testes
    private static final String SECRET        = "dGVzdGVTZWNyZXRLZXlGb3JUZXNkaW5nT25seTEyMzQ=";
    private static final long   EXPIRATION_MS = 3_600_000L; // 1 hora

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    // ── gerarToken ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("gerarToken deve retornar string não nula e não vazia")
    void gerarToken_deveRetornarTokenNaoVazio() {
        String token = jwtService.gerarToken("usuario@teste.com", "ROLE_OPERADOR");

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("gerarToken deve produzir token com três segmentos JWT (header.payload.signature)")
    void gerarToken_deveSerJwtComTresSegmentos() {
        String token = jwtService.gerarToken("usuario@teste.com", "ROLE_OPERADOR");

        // JWT padrão: três partes separadas por ponto
        assertThat(token.split("\\.")).hasSize(3);
    }

    // ── extrairEmail ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("extrairEmail deve retornar o e-mail embutido no token")
    void extrairEmail_deveRetornarEmailCorreto() {
        String email = "admin@exemplo.com";
        String token = jwtService.gerarToken(email, "ROLE_ADMINISTRADOR");

        assertThat(jwtService.extrairEmail(token)).isEqualTo(email);
    }

    // ── extrairRole ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("extrairRole deve retornar a role embutida no token")
    void extrairRole_deveRetornarRoleCorreta() {
        String role  = "ROLE_ADMINISTRADOR";
        String token = jwtService.gerarToken("admin@exemplo.com", role);

        assertThat(jwtService.extrairRole(token)).isEqualTo(role);
    }

    // ── tokenValido ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("tokenValido deve retornar true para token recém-gerado")
    void tokenValido_deveRetornarTrueParaTokenValido() {
        String token = jwtService.gerarToken("usuario@teste.com", "ROLE_OPERADOR");

        assertThat(jwtService.tokenValido(token)).isTrue();
    }

    @Test
    @DisplayName("tokenValido deve retornar false para token expirado")
    void tokenValido_deveRetornarFalseParaTokenExpirado() {
        // Cria JwtService com expiração de -1 ms (já expirado no momento da criação)
        JwtService jwtExpirado = new JwtService(SECRET, -1L);
        String token = jwtExpirado.gerarToken("usuario@teste.com", "ROLE_OPERADOR");

        assertThat(jwtService.tokenValido(token)).isFalse();
    }

    @Test
    @DisplayName("tokenValido deve retornar false para string aleatória")
    void tokenValido_deveRetornarFalseParaTokenInvalido() {
        assertThat(jwtService.tokenValido("token.invalido.qualquer")).isFalse();
    }

    @Test
    @DisplayName("tokenValido deve retornar false para string vazia")
    void tokenValido_deveRetornarFalseParaStringVazia() {
        assertThat(jwtService.tokenValido("")).isFalse();
    }

    // ── consistência entre geração e leitura ──────────────────────────────────

    @Test
    @DisplayName("token gerado deve manter email e role intactos após round-trip")
    void roundTrip_emailERoleDevemSerPreservados() {
        String email = "roundtrip@teste.com";
        String role  = "ROLE_ADMINISTRADOR";

        String token = jwtService.gerarToken(email, role);

        assertThat(jwtService.extrairEmail(token)).isEqualTo(email);
        assertThat(jwtService.extrairRole(token)).isEqualTo(role);
        assertThat(jwtService.tokenValido(token)).isTrue();
    }

    @Test
    @DisplayName("tokens gerados para usuários diferentes devem ser distintos")
    void gerarToken_deveProduzirTokensDistintosParaUsuariosDiferentes() {
        String token1 = jwtService.gerarToken("a@teste.com", "ROLE_OPERADOR");
        String token2 = jwtService.gerarToken("b@teste.com", "ROLE_OPERADOR");

        assertThat(token1).isNotEqualTo(token2);
    }
}
