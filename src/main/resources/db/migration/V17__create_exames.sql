CREATE TABLE exames (
    id BIGSERIAL PRIMARY KEY,
    paciente_id INTEGER NOT NULL REFERENCES usuarios(id),
    profissional_cadastro_id INTEGER NOT NULL REFERENCES usuarios(id),
    data_coleta DATE NOT NULL,
    laboratorio VARCHAR(200),
    observacao TEXT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('RASCUNHO','AGENDADO','PUBLICADO','INATIVADO')),
    disponibilizacao_em TIMESTAMP WITH TIME ZONE,
    publicado_em TIMESTAMP WITH TIME ZONE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_exames_paciente ON exames(paciente_id, data_coleta DESC);
CREATE INDEX idx_exames_agendados ON exames(disponibilizacao_em) WHERE status = 'AGENDADO';

CREATE TABLE arquivos_exame (
    id BIGSERIAL PRIMARY KEY,
    exame_id BIGINT NOT NULL REFERENCES exames(id),
    nome_original VARCHAR(255) NOT NULL,
    chave_armazenamento VARCHAR(255) NOT NULL UNIQUE,
    mime_type VARCHAR(80) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    checksum VARCHAR(64) NOT NULL,
    enviado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE UNIQUE INDEX idx_arquivo_exame_ativo ON arquivos_exame(exame_id) WHERE ativo;

CREATE TABLE eventos_exame (
    id BIGSERIAL PRIMARY KEY,
    exame_id BIGINT NOT NULL REFERENCES exames(id),
    autor_id INTEGER REFERENCES usuarios(id),
    acao VARCHAR(50) NOT NULL,
    ocorrido_em TIMESTAMP WITH TIME ZONE NOT NULL,
    detalhes TEXT
);
CREATE INDEX idx_eventos_exame ON eventos_exame(exame_id, ocorrido_em);
