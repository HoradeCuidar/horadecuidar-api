package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import java.time.LocalDate;

public record DadosEvolucaoSemanalNutricional(
        LocalDate inicioSemana,
        long esperado,
        long realizado,
        long naoRealizado,
        long semRegistro
) {
}
