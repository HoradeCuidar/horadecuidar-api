package com.hdc.hdc.relatorio.orientacao.dto;

import java.time.LocalDate;

public record ResumoFuncionalDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Integer quantidade,
        Integer tempoRealizacao
) {
}

