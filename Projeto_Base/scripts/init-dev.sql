-- ============================================================
-- SCRIPT DE INICIALIZAÇÃO — AMBIENTE DE DESENVOLVIMENTO LOCAL
-- ============================================================
--
-- QUANDO EXECUTA:
--   Este script é executado pelo MySQL automaticamente na PRIMEIRA
--   vez que o container sobe com o volume "mysql_data" vazio.
--   Em inicializações subsequentes (volume já existente) ele é ignorado.
--
-- PARA REEXECUTAR (ex: após alterar este arquivo):
--   docker compose down -v   ← apaga o volume e os dados
--   docker compose up -d     ← recria tudo do zero
--
-- SCHEMA:
--   Espelha a entidade br.com.luismatos.domain.model.Usuario.java.
--   O Hibernate (hbm2ddl.auto=update) reutiliza esta tabela ao subir
--   e adiciona colunas novas se necessário, sem apagar dados existentes.
--
-- ============================================================

USE agenda;

-- ── Tabela de usuários ───────────────────────────────────────
-- Mapeamento dos campos de Usuario.java:
--   @Id @GeneratedValue(IDENTITY)  → ID BIGINT AUTO_INCREMENT
--   @Column(name="EMAIL", unique)  → EMAIL VARCHAR(255) UNIQUE
--   @Column(name="SENHA")          → SENHA VARCHAR(255)
--   @Enumerated(EnumType.STRING)
--   @Column(name="TIPOUSUARIO")    → TIPOUSUARIO VARCHAR(50)  [valores: COMUM | ADMINISTRADOR]
--   @Transient confirmaSenha       → NÃO persiste (campo de UI)
CREATE TABLE IF NOT EXISTS tb01_usuario (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    EMAIL       VARCHAR(255) UNIQUE,
    SENHA       VARCHAR(255),
    TIPOUSUARIO VARCHAR(50),
    PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Usuário de teste ─────────────────────────────────────────
--
--  email  : teste
--  senha  : teste
--  tipo   : ADMINISTRADOR  (acesso total para validar todas as funcionalidades)
--
--  COMO O HASH FOI GERADO:
--    Classe    : br.com.luismatos.infrastructure.security.Sha256PasswordEncoder
--    Algoritmo : SHA-256
--    Encoding  : rawPassword.getBytes("UTF-8")
--    Formato   : String.format("%02X", 0xFF & b)  → hex maiúsculo, 2 dígitos por byte
--
--    Comando equivalente (bash):
--      printf '%s' "teste" | sha256sum | awk '{print toupper($1)}'
--
--    Verificado com três ferramentas independentes:
--      sha256sum  → 46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5
--      openssl    → 46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5
--      Python3    → 46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5
--
INSERT INTO tb01_usuario (EMAIL, SENHA, TIPOUSUARIO)
SELECT
    'teste@teste.com.br',
    '46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5',
    'ADMINISTRADOR'
WHERE NOT EXISTS (
    SELECT 1 FROM tb01_usuario WHERE EMAIL = 'teste@teste.com.br'
);
