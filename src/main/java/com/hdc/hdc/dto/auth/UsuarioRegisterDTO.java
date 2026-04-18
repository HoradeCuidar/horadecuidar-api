package com.hdc.hdc.dto.auth;

import com.hdc.hdc.model.enums.Role;

public record UsuarioRegisterDTO(
    String username,
    String email,
    String senha,
    Role role
) {}
