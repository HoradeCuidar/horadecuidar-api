package com.hdc.hdc.usuarios.dto;

import com.hdc.hdc.usuarios.enums.Genero;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;

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
