package com.hdc.hdc.prescricao_nutricional.alimento.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnidadeDeMedida {
    G("g"),
    KG("kg"),
    ML("ml"),
    L("Litro"),
    UNIDADE("Unidade"),
    FATIA("Fatia"),
    COLHER("Colher"),
    XICARA("Xícara");

    public final String unidadeDeMedida;
}