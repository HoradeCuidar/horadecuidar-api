package com.hdc.hdc.profissionais_saude.dto;

import com.hdc.hdc.usuarios.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfissionalDaSaudeResponseDTO {

    private Integer id;
    private String nome;
    private String username;
    private String dataDeNascimento;
    private Integer idade;
    private Role role;
    private String status;
    private String telefone;
    private String rua;
    private String bairro;
    private String estado;
    private String cidade;
    private String numeroDaCasa;
    private String genero;
    private String email;
    private String fotoDePerfil;
}