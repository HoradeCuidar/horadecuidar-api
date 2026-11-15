ALTER TABLE prescricao_nutricional
    ADD COLUMN modelo_origem_id INTEGER,
    ADD COLUMN modelo_origem_versao INTEGER,
    ADD COLUMN modelo_origem_nome VARCHAR(160);
-- Sem FK: a origem é informativa e continua legível mesmo se o modelo for excluído.
