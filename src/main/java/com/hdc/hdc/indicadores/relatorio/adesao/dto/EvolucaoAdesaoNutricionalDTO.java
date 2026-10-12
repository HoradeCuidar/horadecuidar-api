package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import java.time.LocalDate;
import java.util.List;

public record EvolucaoAdesaoNutricionalDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        String agrupamento,
        List<PeriodoEvolucaoNutricionalDTO> periodos
) {
}
