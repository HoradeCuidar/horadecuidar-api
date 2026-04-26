package com.hdc.hdc.prescricao_medicamentos.enums;

import lombok.Getter;

@Getter
public enum ViaAdministracao {
    ORAL("oral"),
    SUBLINGUAL("sublingual"),
    TOPICA("topica"),
    OFTALMICA("oftalmica"),
    NASAL("nasal"),
    INALATORIA("inalatoria"),
    RETAL("retal"),
    INJETAVEL("injetavel");

    private final String name;

    ViaAdministracao(String name) {
        this.name = name;
    }
}
