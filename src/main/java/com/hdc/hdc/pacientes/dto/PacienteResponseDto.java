package com.hdc.hdc.pacientes.dto;

import com.hdc.hdc.adesao.classificacao.ClassificacaoAdesao;
import com.hdc.hdc.doencas.Doenca;
import com.hdc.hdc.usuarios.enums.Role;

import java.util.List;

public record PacienteResponseDto(
    Integer id,
    String nome,
    String email,
    String username,
    ClassificacaoAdesao classificacao,
    String dataDeNascimento,
    Integer idade,
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
    String observacoes,
    String fotoDePerfil
) {
}
