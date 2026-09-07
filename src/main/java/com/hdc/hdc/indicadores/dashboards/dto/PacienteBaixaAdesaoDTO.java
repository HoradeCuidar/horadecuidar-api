package com.hdc.hdc.indicadores.dashboards.dto;

import java.math.BigDecimal;

public record PacienteBaixaAdesaoDTO(
        Integer pacienteId,
        String nome,
        BigDecimal percentualAdesao,
        Integer realizado,
        Integer esperado
) {
}
