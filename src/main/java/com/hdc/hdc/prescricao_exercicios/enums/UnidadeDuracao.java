package com.hdc.hdc.prescricao_exercicios.enums;

import lombok.Getter;

/**
 * Unidade de medida da duração de cada sessão de exercício.
 */
@Getter
public enum UnidadeDuracao {
    MINUTOS("minutos"),
    HORAS("horas");

    private final String descricao;

    UnidadeDuracao(String descricao) {
        this.descricao = descricao;
    }
}
