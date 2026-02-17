package com.hdc.hdc.dto.response;

public record ResetPasswordResponseDto(
        boolean resetado,
        String messagem
) {
}
