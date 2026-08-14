CREATE TABLE registro_adesao_medicamento (
    id BIGSERIAL PRIMARY KEY,
    prescricao_id UUID NOT NULL,
    item_medicacao_id BIGINT NOT NULL,
    data_hora_registro TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    observacao TEXT,
    CONSTRAINT fk_adesao_prescricao FOREIGN KEY (prescricao_id) REFERENCES prescricao_medicamento(id),
    CONSTRAINT fk_adesao_item FOREIGN KEY (item_medicacao_id) REFERENCES item_medicacao(id)
);
