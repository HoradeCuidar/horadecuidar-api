package com.hdc.hdc.pacientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hdc.hdc.usuarios.enums.Genero;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record PacienteCreateDto(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Username é obrigatório")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username não pode conter espaços"
    )
    String username,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String senha,

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dataDeNascimento,

    Integer idade,

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "Telefone deve conter 10 ou 11 dígitos"
    )
    String telefone,

    @NotNull(message = "Gênero é obrigatório")
    Genero genero,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    String rua,
    String bairro,
    String estado,
    String cidade,
    String numeroDaCasa,

    List<Long> doencas,
    String observacoes
) {
}
