package com.hdc.hdc.indicadores.relatorio.adesao.dto;

public record DadosResumoAdesaoMedicamento(
        Integer esperado,
        Integer realizado,
        Integer naoRealizado,
        Integer semRegistro
) {
}
