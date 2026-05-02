package com.hdc.hdc.auth.autenticacao.dto;

import com.hdc.hdc.usuarios.enums.Role;

public record UsuarioRegisterDTO(
    String username,
    String email,
    String senha,
    Role role
) {}
