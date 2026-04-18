package com.hdc.hdc.dto;

import java.time.LocalDateTime;

public record MedicamentoDTO(
        Integer id,
        String nome,
        LocalDateTime criadoEm
) {
}
