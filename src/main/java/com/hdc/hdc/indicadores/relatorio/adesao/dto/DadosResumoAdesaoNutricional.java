package com.hdc.hdc.indicadores.relatorio.adesao.dto;

public record DadosResumoAdesaoNutricional(
        Integer esperado,
        Integer realizado,
        Integer naoRealizado,
        Integer semRegistro
) {
}
