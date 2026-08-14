package com.hdc.hdc.usuarios.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ADMIN("Administrador"),
    PACIENTE("Paciente"),
    PROFISSIONAL_DA_SAUDE("Profissional da Saúde");

    private final String role;
}