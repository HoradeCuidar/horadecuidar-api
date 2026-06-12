package com.hdc.hdc.prescricao_exercicios.enums;

import lombok.Getter;

@Getter
public enum TipoExercicio {
    AEROBICO("Aeróbico"),
    ALONGAMENTO("Alongamento"),
    RESISTENCIA("Resistência / Força"),
    FLEXIBILIDADE("Flexibilidade / Alongamento"),
    EQUILIBRIO("Equilíbrio"),
    FUNCIONAL("Funcional"),
    RELAXAMENTO("Relaxamento / Respiração"),
    OUTRO("Outro");

    private final String descricao;

    TipoExercicio(String descricao) {
        this.descricao = descricao;
    }
}
