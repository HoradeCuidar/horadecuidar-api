package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import java.time.LocalDate;

public record DadosDetalhamentoDiarioMedicamento(
        LocalDate data,
        long esperado,
        long realizado,
        long naoRealizado,
        long semRegistro,
        long totalElementos
) {
}
