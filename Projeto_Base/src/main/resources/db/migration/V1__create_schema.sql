-- ============================================================
-- V1 — Schema inicial do Projeto Base
-- Espelha a entidade br.com.luismatos.domain.model.Usuario
--
-- Usa IF NOT EXISTS: seguro em bancos já existentes
-- (Flyway garante que esta versão só roda uma vez via flyway_schema_history)
-- ============================================================

CREATE TABLE IF NOT EXISTS tb01_usuario (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    EMAIL       VARCHAR(255) NOT NULL UNIQUE,
    SENHA       VARCHAR(255) NOT NULL,
    TIPOUSUARIO VARCHAR(50)  NOT NULL,
    PRIMARY KEY (ID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
