package br.com.luismatos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProjetoBaseApplicationTest {

    @Test
    @DisplayName("Contexto Spring carrega sem erros")
    void contextLoads() {
        // Verifica que todos os beans são criados e o contexto sobe corretamente
    }
}
