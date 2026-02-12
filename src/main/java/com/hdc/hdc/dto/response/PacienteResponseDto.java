package com.hdc.hdc.dto.response;

import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.model.enums.Role;

import java.util.List;

public record PacienteResponseDto(
    Integer id,
    String nome,
    String email,
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
    List<Doenca> doencas,
    String observacoes
) {
}
