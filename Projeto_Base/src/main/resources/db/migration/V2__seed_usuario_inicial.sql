-- ============================================================
-- V2 — Usuário inicial de desenvolvimento
--
-- email : teste
-- senha : teste  (hash SHA-256 uppercase)
-- tipo  : ADMINISTRADOR
--
-- Hash verificado com três ferramentas:
--   printf '%s' "teste" | sha256sum | awk '{print toupper($1)}'
--   → 46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5
--
-- WHERE NOT EXISTS garante idempotência — seguro rodar múltiplas vezes
-- ============================================================

INSERT INTO tb01_usuario (EMAIL, SENHA, TIPOUSUARIO)
SELECT 'teste',
       '46070D4BF934FB0D4B06D9E2C46E346944E322444900A435D7D9A95E6D7435F5',
       'ADMINISTRADOR'
WHERE NOT EXISTS (
    SELECT 1 FROM tb01_usuario WHERE EMAIL = 'teste'
);
