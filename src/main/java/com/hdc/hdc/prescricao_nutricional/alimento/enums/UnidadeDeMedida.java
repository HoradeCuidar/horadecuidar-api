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
    COLHER_SOPA("Colher de sopa"),
    COLHER_CHA("Colher de chá"),
    COLHER_SOBREMESA("Colher de sobremesa"),
    XICARA("Xícara");

    public final String unidadeDeMedida;
}
