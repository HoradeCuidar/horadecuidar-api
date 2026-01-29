package com.hdc.hdc.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Role {

    ADMIN("Administrador"),
    PACIENTE("Paciente"),
    PROFISSIONAL_DA_SAUDE("Profissional da Saúde");

    private final String role;
}