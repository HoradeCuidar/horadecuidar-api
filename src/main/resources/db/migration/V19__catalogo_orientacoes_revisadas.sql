CREATE TABLE categoria_orientacao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);
-- Opções iniciais do piloto; o conteúdo clínico exige revisão profissional.
INSERT INTO categoria_orientacao (nome) VALUES ('Equilíbrio'), ('Mobilidade'), ('Fortalecimento');
ALTER TABLE tag_funcional ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE orientacao_funcional
    ADD COLUMN resumo TEXT;
ALTER TABLE orientacao_funcional ADD COLUMN categoria_id BIGINT REFERENCES categoria_orientacao(id);
ALTER TABLE orientacao_funcional ADD COLUMN duracao_estimada INTEGER CHECK (duracao_estimada > 0);
ALTER TABLE orientacao_funcional ADD COLUMN materiais TEXT;
ALTER TABLE orientacao_funcional ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO'
        CHECK (status IN ('RASCUNHO', 'PUBLICADA', 'INATIVA'));
ALTER TABLE orientacao_funcional ADD COLUMN revisado_por INTEGER REFERENCES usuarios(id);
ALTER TABLE orientacao_funcional ADD COLUMN publicado_em TIMESTAMP;
ALTER TABLE orientacao_funcional ADD COLUMN cuidados_revisados BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE orientacao_funcional ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;
UPDATE orientacao_funcional SET resumo = descricao, status = 'RASCUNHO', ativo = FALSE;
CREATE TABLE passo_orientacao (
    id BIGSERIAL PRIMARY KEY,
    orientacao_id BIGINT NOT NULL REFERENCES orientacao_funcional(id),
    ordem INTEGER NOT NULL,
    instrucao TEXT
);
CREATE TABLE cuidado_orientacao (
    id BIGSERIAL PRIMARY KEY,
    orientacao_id BIGINT NOT NULL REFERENCES orientacao_funcional(id),
    ordem INTEGER NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('PRECAUCAO','NAO_REALIZAR','SINAL_DE_ALERTA')),
    texto TEXT
);
CREATE TABLE midia_orientacao (
    id BIGSERIAL PRIMARY KEY,
    orientacao_id BIGINT NOT NULL REFERENCES orientacao_funcional(id),
    passo_id BIGINT REFERENCES passo_orientacao(id) ON DELETE SET NULL,
    ordem INTEGER NOT NULL,
    url VARCHAR(1000) NOT NULL,
    legenda VARCHAR(500),
    texto_alternativo VARCHAR(500),
    capa BOOLEAN NOT NULL DEFAULT FALSE
);
INSERT INTO midia_orientacao (orientacao_id, ordem, url, capa)
SELECT id, 0, url_imagem, TRUE FROM orientacao_funcional WHERE NULLIF(TRIM(url_imagem), '') IS NOT NULL;
CREATE TABLE historico_orientacao (
    id BIGSERIAL PRIMARY KEY,
    orientacao_id BIGINT NOT NULL REFERENCES orientacao_funcional(id),
    autor_id INTEGER NOT NULL REFERENCES usuarios(id),
    data_alteracao TIMESTAMP NOT NULL,
    acao VARCHAR(50) NOT NULL,
    conteudo_anterior TEXT NOT NULL
);
CREATE INDEX idx_orientacao_status_categoria ON orientacao_funcional(status, categoria_id);
CREATE INDEX idx_passos_orientacao ON passo_orientacao(orientacao_id);
CREATE INDEX idx_cuidados_orientacao ON cuidado_orientacao(orientacao_id);
CREATE INDEX idx_midias_orientacao ON midia_orientacao(orientacao_id);
CREATE INDEX idx_historico_orientacao ON historico_orientacao(orientacao_id, data_alteracao);
