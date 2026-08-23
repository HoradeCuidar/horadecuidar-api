package com.hdc.hdc.indicadores.dashboards.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PrescricaoProximaVencimentoDTO(
        UUID prescricaoId,
        Integer pacienteId,
        String nomePaciente,
        LocalDate dataFim,
        long diasRestantes
) {
}
