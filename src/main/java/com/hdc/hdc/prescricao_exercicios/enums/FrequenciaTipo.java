package com.hdc.hdc.prescricao_exercicios.enums;

import lombok.Getter;

/**
 * Tipo da frequência de realização do exercício.
 * Equivalente ao IntervaloTipo do módulo de medicamentos,
 * mas com semântica orientada a sessões de exercício.
 */
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
