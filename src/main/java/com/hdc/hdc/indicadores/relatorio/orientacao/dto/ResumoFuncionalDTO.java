package com.hdc.hdc.indicadores.relatorio.orientacao.dto;

import java.time.LocalDate;

public record ResumoFuncionalDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Integer quantidade,
        Integer tempoRealizacao
) {
}

