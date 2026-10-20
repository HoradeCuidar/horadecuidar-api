CREATE TABLE resumo_adesao_paciente (
    id BIGSERIAL PRIMARY KEY,
    paciente_id INTEGER NOT NULL UNIQUE,
    periodo_inicio DATE,
    periodo_fim DATE,
    esperado INTEGER,
    realizado INTEGER,
    nao_realizado INTEGER,
    sem_registro INTEGER,
    percentual DECIMAL(5,2),
    classificacao VARCHAR(50),
    calculado_em TIMESTAMP,

    CONSTRAINT fk_resumo_adesao_paciente FOREIGN KEY (paciente_id) REFERENCES usuarios(id)
);
