package com.hdc.hdc.auth.recuperacao_senha.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitarRecuperacaoDto(
        @NotBlank(message = "O e-mail de recuperação deve ser informado.")
        String email
) {
}
