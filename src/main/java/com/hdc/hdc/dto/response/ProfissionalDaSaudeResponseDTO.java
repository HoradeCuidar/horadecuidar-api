package com.hdc.hdc.dto.response;

import com.hdc.hdc.model.enums.Role;
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
}