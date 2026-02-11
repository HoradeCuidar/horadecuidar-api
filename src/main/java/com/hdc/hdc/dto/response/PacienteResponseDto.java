package com.hdc.hdc.dto.response;

import com.hdc.hdc.model.enums.Role;

public record PacienteResponseDto(
    Integer id,
    String nome,
    String username,
    String dataDeNascimento,
    Role role,
    String status,
    String telefone,
    String rua,
    String bairro,
    String estado,
    String cidade,
    String numeroDaCasa,
    String genero,
    String email
) {
}
