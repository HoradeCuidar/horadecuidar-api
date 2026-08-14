package com.hdc.hdc.adesao.classificacao.dto;

public record DadosCalculoAdesaoDTO(
        Integer esperado,
        Integer realizado,
        Integer naoRealizado,
        Integer semRegistro
) {
}
