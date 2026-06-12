package com.hdc.hdc.prescricao_exercicios.enums;

import lombok.Getter;

@Getter
public enum FrequenciaTipo {
    DIA("dia"),
    SEMANA("semana"),
    MES("mês");

    private final String descricao;

    FrequenciaTipo(String descricao) {
        this.descricao = descricao;
    }
}
