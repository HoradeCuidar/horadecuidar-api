CREATE TABLE doencas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(244) NOT NULL UNIQUE
);

INSERT INTO doencas (id, nome) VALUES
    (1, 'Hipertensão Arterial'),
    (2, 'Diabetes Mellitus Tipo 1'),
    (3, 'Diabetes Mellitus Tipo 2'),
    (4, 'Dislipidemia'),
    (5, 'Obesidade'),
    (6, 'Asma'),
    (7, 'Doença Pulmonar Obstrutiva Crônica (DPOC)'),
    (8, 'Hipotireoidismo'),
    (9, 'Hipertireoidismo'),
    (10, 'Insuficiência Cardíaca'),
    (11, 'Doença Arterial Coronariana'),
    (12, 'Doença Renal Crônica'),
    (13, 'Epilepsia'),
    (14, 'Depressão'),
    (15, 'Ansiedade'),
    (16, 'Artrite / Artrose'),
    (17, 'Osteoporose'),
    (18, 'Outra (não especificada)');

SELECT setval('doencas_id_seq', (SELECT MAX(id) FROM doencas));

CREATE TABLE paciente_doencas (
    paciente_id INTEGER NOT NULL,
    doenca_id BIGINT NOT NULL,
    PRIMARY KEY (paciente_id, doenca_id),
    CONSTRAINT fk_paciente_doencas_usuario FOREIGN KEY (paciente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_paciente_doencas_doenca FOREIGN KEY (doenca_id) REFERENCES doencas(id) ON DELETE CASCADE
);
