package com.hdc.hdc.relatorio.adesao.dto;

import java.time.LocalDate;

public record DadosEvolucaoSemanalMedicamento(
        LocalDate inicioSemana,
        long esperado,
        long realizado,
        long naoRealizado,
        long semRegistro
) {
}
