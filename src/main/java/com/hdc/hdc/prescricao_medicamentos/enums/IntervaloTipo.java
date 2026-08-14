package com.hdc.hdc.prescricao_medicamentos.enums;

import lombok.Getter;

@Getter
public enum IntervaloTipo {
    HORA("hora"),
    DIA("dia"),
    SEMANA("semana"),
    MES("mes");

    private final String value;

    IntervaloTipo(String value) {
        this.value = value;
    }
}
