package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodoEvolucaoNutricionalDTO(
        LocalDate inicioSemana,
        LocalDate fimSemana,
        LocalDate inicioConsiderado,
        LocalDate fimConsiderado,
        long esperado,
        long realizado,
        long naoRealizado,
        long semRegistro,
        BigDecimal percentualAdesao
) {
}
