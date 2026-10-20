CREATE TABLE ocorrencia_refeicao (
    id SERIAL PRIMARY KEY,
    prescricao_id INTEGER NOT NULL,
    refeicao_id INTEGER NOT NULL,
    opcao_id INTEGER,
    data_prevista DATE NOT NULL,
    ordem_no_dia INT NOT NULL,
    data_hora_registro TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    observacao TEXT,

    CONSTRAINT fk_ocorrencia_refeicao_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_nutricional(id),
    CONSTRAINT fk_ocorrencia_refeicao_refeicao FOREIGN KEY (refeicao_id) REFERENCES refeicao(id),
    CONSTRAINT fk_ocorrencia_refeicao_opcao FOREIGN KEY (opcao_id) REFERENCES opcao_refeicao(id),
    CONSTRAINT uk_ocorrencia_refeicao_data UNIQUE (refeicao_id, data_prevista)
);
