CREATE TABLE prescricao_nutricional (
    id SERIAL PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    profissional_id INTEGER NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    data_encerramento DATE,
    status VARCHAR(20) NOT NULL,
    observacoes TEXT,

    CONSTRAINT fk_prescricao_paciente FOREIGN KEY (paciente_id) REFERENCES usuarios(id),
    CONSTRAINT fk_prescricao_profissional FOREIGN KEY (profissional_id) REFERENCES usuarios(id)
);

CREATE TABLE refeicao (
    id SERIAL PRIMARY KEY,
    prescricao_id INTEGER NOT NULL,
    nome VARCHAR(100) NOT NULL,
    ordem INTEGER NOT NULL,
    observacoes TEXT,

    CONSTRAINT fk_refeicao_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_nutricional(id)
);

CREATE TABLE opcao_refeicao (
    id SERIAL PRIMARY KEY,
    refeicao_id INTEGER NOT NULL,
    ordem INTEGER NOT NULL,
    descricao TEXT,

    CONSTRAINT fk_opcao_refeicao FOREIGN KEY (refeicao_id) REFERENCES refeicao(id)
);

CREATE TABLE alimento_prescrito (
    id SERIAL PRIMARY KEY,
    opcao_id INTEGER NOT NULL,
    descricao TEXT NOT NULL,
    quantidade NUMERIC(8,2),
    unidade VARCHAR(30),
    observacao TEXT,

    CONSTRAINT fk_alimento_opcao FOREIGN KEY (opcao_id) REFERENCES opcao_refeicao(id)
);