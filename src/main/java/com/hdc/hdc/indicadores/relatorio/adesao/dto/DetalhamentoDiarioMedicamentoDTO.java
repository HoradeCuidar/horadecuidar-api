package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetalhamentoDiarioMedicamentoDTO(
        LocalDate data,
        String tipo,
        long esperado,
        long realizado,
        long naoRealizado,
        long semRegistro,
        BigDecimal percentualAdesao
) {
}
