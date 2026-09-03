-- Consolida eventuais duplicidades legadas, preservando primeiro registros do
-- paciente e, depois, a ocorrência operacional mais relevante.
WITH ocorrencias_ranqueadas AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY item_medicacao_id, data_prevista, ordem_no_dia
            ORDER BY
                CASE status
                    WHEN 'REALIZADO' THEN 1
                    WHEN 'NAO_REALIZADO' THEN 2
                    WHEN 'PENDENTE' THEN 3
                    ELSE 4
                END,
                data_hora_registro DESC NULLS LAST,
                id
        ) AS ordem
    FROM ocorrencia_medicamento
)
DELETE FROM ocorrencia_medicamento ocorrencia
USING ocorrencias_ranqueadas ranqueada
WHERE ocorrencia.id = ranqueada.id
  AND ranqueada.ordem > 1;

ALTER TABLE ocorrencia_medicamento
    ADD CONSTRAINT uk_ocorrencia_item_data_ordem
        UNIQUE (item_medicacao_id, data_prevista, ordem_no_dia);

UPDATE item_medicacao
SET ativo = TRUE
WHERE ativo IS NULL;

ALTER TABLE item_medicacao
    ALTER COLUMN ativo SET DEFAULT TRUE,
    ALTER COLUMN ativo SET NOT NULL;
