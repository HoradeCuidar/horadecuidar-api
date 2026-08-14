-- V9: Módulo de Prescrições de Exercícios Físicos

CREATE TABLE prescricao_exercicio (
    id              UUID        PRIMARY KEY,
    paciente_id     INTEGER     NOT NULL,
    profissional_id INTEGER     NOT NULL,
    data_inicio     TIMESTAMP   NOT NULL,
    data_fim        TIMESTAMP,
    observacao      TEXT,
    ativo           BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_prescricao_exercicio_paciente     FOREIGN KEY (paciente_id)     REFERENCES usuarios(id),
    CONSTRAINT fk_prescricao_exercicio_profissional FOREIGN KEY (profissional_id) REFERENCES usuarios(id)
);

CREATE TABLE item_exercicio (
    id                  BIGSERIAL   PRIMARY KEY,
    prescricao_id       UUID        NOT NULL,
    nome_exercicio      VARCHAR(255) NOT NULL,
    tipo_exercicio      VARCHAR(50)  NOT NULL,
    frequencia_valor    INTEGER      NOT NULL,
    frequencia_tipo     VARCHAR(20)  NOT NULL,
    duracao_valor       INTEGER      NOT NULL,
    unidade_duracao     VARCHAR(20)  NOT NULL,
    series              INTEGER,
    repeticoes          INTEGER,
    intensidade         VARCHAR(100),
    observacao          TEXT,
    CONSTRAINT fk_item_exercicio_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_exercicio(id)
);

CREATE TABLE realizacao_exercicio (
    id                          BIGSERIAL   PRIMARY KEY,
    item_exercicio_id           BIGINT      NOT NULL,
    paciente_id                 INTEGER     NOT NULL,
    status                      VARCHAR(30) NOT NULL,
    duracao_realizada_minutos   INTEGER,
    observacao                  TEXT,
    data_registro               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_realizacao_item    FOREIGN KEY (item_exercicio_id) REFERENCES item_exercicio(id),
    CONSTRAINT fk_realizacao_paciente FOREIGN KEY (paciente_id)      REFERENCES usuarios(id)
    -- Garante no banco que não pode haver dois registros do mesmo item para o mesmo paciente no mesmo dia
    -- CONSTRAINT uq_realizacao_item_paciente_dia UNIQUE (item_exercicio_id, paciente_id, CAST(data_registro AS DATE))
);

CREATE UNIQUE INDEX uq_realizacao_item_paciente_dia
    ON realizacao_exercicio (item_exercicio_id, paciente_id, CAST(data_registro AS DATE));
