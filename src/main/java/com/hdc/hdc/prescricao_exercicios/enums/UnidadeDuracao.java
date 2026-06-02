package com.hdc.hdc.prescricao_exercicios.enums;

import lombok.Getter;

@Getter
public enum UnidadeDuracao {
    MINUTOS("minutos"),
    HORAS("horas");

    private final String descricao;

    UnidadeDuracao(String descricao) {
        this.descricao = descricao;
    }
}
