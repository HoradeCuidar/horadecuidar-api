package com.hdc.hdc.dto;

import com.hdc.hdc.model.enums.Genero;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;

public record UsuarioDTO(
        Integer id,
        String nome,
        String username,
        Role role,
        Status status,
        String telefone,
        Genero genero,
        String email,
        String fotoDePerfil
) {
}
