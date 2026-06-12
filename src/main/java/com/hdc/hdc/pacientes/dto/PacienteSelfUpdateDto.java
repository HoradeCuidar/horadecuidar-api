package com.hdc.hdc.pacientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hdc.hdc.usuarios.enums.Genero;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PacienteSelfUpdateDto(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "Telefone deve conter 10 ou 11 dígitos"
    )
    String telefone,

    @NotNull(message = "Gênero é obrigatório")
    Genero genero,

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dataDeNascimento,

    String rua,
    String bairro,
    String estado,
    String cidade,
    String numeroDaCasa
) {
}
