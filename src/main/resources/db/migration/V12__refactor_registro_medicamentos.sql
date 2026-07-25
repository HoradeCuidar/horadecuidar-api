DROP TABLE IF EXISTS registro_adesao_medicamento CASCADE;

CREATE TABLE ocorrencia_medicacao (
    id BIGSERIAL PRIMARY KEY,
    prescricao_id UUID NOT NULL,
    item_medicacao_id BIGINT NOT NULL,
    data_prevista DATE NOT NULL,
    ordem_no_dia INT NOT NULL,
    data_hora_registro TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    observacao TEXT,
    CONSTRAINT fk_adesao_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_medicamento(id),
    CONSTRAINT fk_adesao_item FOREIGN KEY (item_medicacao_id) REFERENCES item_medicacao(id)
);
