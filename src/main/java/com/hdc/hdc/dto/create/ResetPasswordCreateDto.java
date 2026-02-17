package com.hdc.hdc.dto.create;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordCreateDto(
        @NotBlank(message = "O token de recuperação deve ser informado.")
        String token,

        @NotBlank(message = "A nova senha deve ser informada.")
        String novaSenha,

        @NotBlank(message = "A confirmacao de senha deve ser informada.")
        String confirmacao
) {
}
