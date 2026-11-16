-- Impede duas prescrições nutricionais ativas para o mesmo participante,
-- inclusive em requisições concorrentes. Conferir duplicidades em bancos compartilhados
-- antes de aplicar esta migração; não há exclusão automática de dados.
CREATE UNIQUE INDEX uk_prescricao_nutricional_ativa_paciente
    ON prescricao_nutricional (paciente_id)
    WHERE status = 'ATIVA';
