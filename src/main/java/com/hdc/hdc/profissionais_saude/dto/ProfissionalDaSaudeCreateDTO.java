package com.hdc.hdc.profissionais_saude.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hdc.hdc.usuarios.enums.Genero;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfissionalDaSaudeCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Username é obrigatório")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username não pode conter espaços"
    )
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDeNascimento;

    private Integer  idade;

    @NotBlank(message = "O contato do profissional é obrigatório")
    @Pattern(
            regexp = "^$|^\\d{10,11}$",
            message = "Telefone deve conter 10 ou 11 dígitos"
    )
    private String telefone;

    private String rua;

    private String bairro;


    @Pattern(regexp = "^$|^[A-Za-z]{2}$", message = "Estado deve conter 2 letras")
    private String estado;

    private String cidade;

    private String numeroDaCasa;

    @NotNull(message = "Gênero é obrigatório")
    private Genero genero;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
}
