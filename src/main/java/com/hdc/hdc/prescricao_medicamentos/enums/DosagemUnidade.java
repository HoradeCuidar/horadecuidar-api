package com.hdc.hdc.prescricao_medicamentos.enums;

import lombok.Getter;

@Getter
public enum DosagemUnidade {
    COMPRIMIDO("comprimido"),
    CAPSULA("cápsula"),
    DRAGEA("drágea"),
    MG("miligramas"),
    G("gramas"),
    ML("mililitros"),
    GOTAS("gotas"),
    COLHER_CHA("colher de chá"),
    COLHER_SOPA("colher de sopa"),;

    private final String value;

    DosagemUnidade(String value) {
        this.value = value;
    }
}
