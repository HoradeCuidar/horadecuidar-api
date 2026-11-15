ALTER TABLE refeicao ADD COLUMN horario TIME;
ALTER TABLE opcao_refeicao ADD COLUMN orientacoes_preparo TEXT;
ALTER TABLE alimento_prescrito ADD COLUMN medida_caseira TEXT;
CREATE TABLE substituicao_alimentar (
    id SERIAL PRIMARY KEY,
    alimento_id INTEGER NOT NULL REFERENCES alimento_prescrito(id),
    descricao TEXT NOT NULL,
    quantidade NUMERIC(8,2) NOT NULL,
    unidade VARCHAR(30) NOT NULL,
    medida_caseira TEXT,
    observacao TEXT,
    ordem INTEGER NOT NULL
);
CREATE INDEX idx_substituicao_alimento ON substituicao_alimentar(alimento_id);
