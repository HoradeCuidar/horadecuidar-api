package com.hdc.hdc.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    public final String status;
}