package com.hdc.hdc.indicadores.dashboards.dto;

public record DashboardResumoDTO(
        Integer participantesAtivos,
        Integer prescricoesAtivas,
        Integer prescricoesProximasVencimento,
        Integer participantesBaixaAdesao
) {
}
