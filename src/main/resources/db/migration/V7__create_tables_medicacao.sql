CREATE TABLE medicamentos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prescricao_medicamento (
    id UUID PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    profissional_id INTEGER NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP,
    observacao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_prescricao_paciente FOREIGN KEY (paciente_id) REFERENCES usuarios(id),
    CONSTRAINT fk_prescricao_profissional FOREIGN KEY (profissional_id) REFERENCES usuarios(id)
);

CREATE TABLE item_medicacao (
    id BIGSERIAL PRIMARY KEY,
    prescricao_id UUID NOT NULL,
    medicamento_id INTEGER,
    nome_medicamento VARCHAR(255) NOT NULL,
    dosagem_valor DOUBLE PRECISION NOT NULL,
    dosagem_unidade VARCHAR(50) NOT NULL,
    quantidade_doses INTEGER,
    intervalo_valor INTEGER NOT NULL,
    intervalo_tipo VARCHAR(50) NOT NULL,
    via_administracao VARCHAR(50) NOT NULL,
    observacao TEXT,
    CONSTRAINT fk_item_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_medicamento(id),
    CONSTRAINT fk_item_medicamento FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)
);
