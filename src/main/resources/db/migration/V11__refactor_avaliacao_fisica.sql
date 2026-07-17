-- V11: Refatoração do módulo funcional
CREATE TABLE IF NOT EXISTS tag_funcional (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
);

DROP TABLE IF EXISTS avaliacao_indicacao_tag CASCADE;
DROP TABLE IF EXISTS avaliacao_fisica CASCADE;

CREATE TABLE avaliacao_fisica (
    id                    BIGSERIAL PRIMARY KEY,
    paciente_id           INTEGER      NOT NULL,
    profissional_id       INTEGER      NOT NULL,
    realiza_atividade     BOOLEAN      NOT NULL,
    atividade_realizada   TEXT,
    frequencia_semanal    INTEGER,
    flexibilidade         VARCHAR(50)  NOT NULL,
    forca_palmar_direita  DECIMAL(5,2),
    forca_palmar_esquerda DECIMAL(5,2),
    assimetria_palmar     VARCHAR(10),
    forca_joelho_direita  DECIMAL(5,2),
    forca_joelho_esquerda DECIMAL(5,2),
    assimetria_joelho     VARCHAR(50),
    queixas               TEXT,
    observacoes           TEXT,
    orientacoes           TEXT,
    data_registro         TIMESTAMP    NOT NULL,
    data_atualizacao      TIMESTAMP,
    CONSTRAINT fk_avaliacao_fisica_paciente      FOREIGN KEY (paciente_id)     REFERENCES usuarios(id),
    CONSTRAINT fk_avaliacao_fisica_profissional  FOREIGN KEY (profissional_id) REFERENCES usuarios(id)
);

CREATE TABLE avaliacao_indicacao_tag (
    avaliacao_fisica_id BIGINT NOT NULL,
    tag_funcional_id    BIGINT NOT NULL,
    PRIMARY KEY (avaliacao_fisica_id, tag_funcional_id),
    CONSTRAINT fk_avaliacao_indicacao_tag_avaliacao FOREIGN KEY (avaliacao_fisica_id) REFERENCES avaliacao_fisica(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_indicacao_tag_tag       FOREIGN KEY (tag_funcional_id)    REFERENCES tag_funcional(id)   ON DELETE CASCADE
);

DROP TABLE IF EXISTS orientacao_tag CASCADE;
DROP TABLE IF EXISTS orientacao_funcional CASCADE;

CREATE TABLE orientacao_funcional (
    id               BIGSERIAL PRIMARY KEY,
    responsavel_id   INTEGER      NOT NULL,
    nome             VARCHAR(100) NOT NULL,
    descricao        TEXT,
    finalidade       TEXT,
    url_imagem       VARCHAR(500),
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    data_criacao     TIMESTAMP    NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_orientacao_funcional_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios(id)
);

CREATE TABLE orientacao_tag (
    orientacao_funcional_id BIGINT NOT NULL,
    tag_funcional_id        BIGINT NOT NULL,
    PRIMARY KEY (orientacao_funcional_id, tag_funcional_id),
    CONSTRAINT fk_orientacao_tag_orientacao FOREIGN KEY (orientacao_funcional_id) REFERENCES orientacao_funcional(id) ON DELETE CASCADE,
    CONSTRAINT fk_orientacao_tag_tag        FOREIGN KEY (tag_funcional_id)        REFERENCES tag_funcional(id)        ON DELETE CASCADE
);

DROP TABLE IF EXISTS realizacao_exercicio CASCADE;

CREATE TABLE realizacao_exercicio (
    id                        BIGSERIAL   PRIMARY KEY,
    orientacao_funcional_id   BIGINT      NOT NULL,
    paciente_id               INTEGER     NOT NULL,
    status                    VARCHAR(30) NOT NULL,
    duracao_realizada_minutos INTEGER,
    sensacao_final            VARCHAR(30),
    observacao                TEXT,
    data_registro             TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_realizacao_orientacao FOREIGN KEY (orientacao_funcional_id) REFERENCES orientacao_funcional(id),
    CONSTRAINT fk_realizacao_paciente   FOREIGN KEY (paciente_id)             REFERENCES usuarios(id)
);

-- Drop antiga tabela exercicios_orientacao se existir (substituída pelo novo módulo)
DROP TABLE IF EXISTS exercicios_orientacao CASCADE;
