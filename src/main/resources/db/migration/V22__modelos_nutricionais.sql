CREATE TABLE modelo_nutricional (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(160) NOT NULL,
    referencia_kcal INTEGER,
    descricao TEXT,
    refeicoes_json TEXT NOT NULL,
    versao INTEGER NOT NULL DEFAULT 1,
    revisao INTEGER NOT NULL DEFAULT 0,
    criado_por_id INTEGER NOT NULL REFERENCES usuarios(id),
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_modelo_referencia_kcal CHECK (referencia_kcal IS NULL OR referencia_kcal > 0),
    CONSTRAINT ck_modelo_versao CHECK (versao > 0)
);
CREATE INDEX idx_modelo_nutricional_nome ON modelo_nutricional (nome);
