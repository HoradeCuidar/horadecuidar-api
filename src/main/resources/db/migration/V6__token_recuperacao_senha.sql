CREATE TABLE token_recuperacao (
    id SERIAL PRIMARY KEY,
    token VARCHAR(244) NOT NULL,
    usuario_id BIGINT NOT NULL,
    expiracao TIMESTAMP NOT NULL,
    usado BOOLEAN NOT NULL
);