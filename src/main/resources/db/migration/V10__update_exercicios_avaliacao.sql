-- Drop old tables related to "prescrição de exercícios"
DROP TABLE IF EXISTS realizacao_exercicio CASCADE;
DROP TABLE IF EXISTS item_exercicio CASCADE;
DROP TABLE IF EXISTS prescricao_exercicio CASCADE;

-- Create table "avaliacao_fisica"
CREATE TABLE avaliacao_fisica (
    id UUID PRIMARY KEY,
    paciente_id INTEGER NOT NULL,
    profissional_id INTEGER NOT NULL,
    realiza_atividade BOOLEAN NOT NULL,
    atividade_realizada TEXT,
    frequencia_semanal INTEGER,
    flexibilidade VARCHAR(50) NOT NULL,
    forca_palmar_direita VARCHAR(10),
    forca_palmar_esquerda VARCHAR(10),
    assimetria_palmar VARCHAR(10),
    forca_joelho_direita VARCHAR(10),
    forca_joelho_esquerda VARCHAR(10),
    assimetria_joelho VARCHAR(50),
    queixas TEXT,
    observacoes TEXT,
    orientacoes TEXT,
    data_registro DATE NOT NULL,
    data_atualizacao DATE,
    CONSTRAINT fk_avaliacao_fisica_paciente FOREIGN KEY (paciente_id) REFERENCES usuarios(id),
    CONSTRAINT fk_avaliacao_fisica_profissional FOREIGN KEY (profissional_id) REFERENCES usuarios(id)
);

-- Create table "exercicios_orientacao"
CREATE TABLE exercicios_orientacao (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    finalidade TEXT,
    categoria VARCHAR(50) NOT NULL,
    descricao TEXT,
    url_imagem VARCHAR(255)
);
