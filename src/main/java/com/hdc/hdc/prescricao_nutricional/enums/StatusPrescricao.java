package com.hdc.hdc.prescricao_nutricional.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusPrescricao {
    ATIVA("Ativa"),
    ENCERRADA("Encerrada");

    public final String statusPrescricao;
}