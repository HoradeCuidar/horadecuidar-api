CREATE TABLE recuperacao_senha (
    id SERIAL PRIMARY KEY,
    token VARCHAR(244) NOT NULL,
    usuario_id BIGINT NOT NULL,
    expiracao DATE NOT NULL,
    usado BOOLEAN NOT NULL
);