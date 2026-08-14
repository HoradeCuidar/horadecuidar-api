package com.hdc.hdc.auth.autenticacao.dto;

import com.hdc.hdc.usuarios.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private Integer id;
    private String token;
    private Role role;
}