package com.hdc.hdc.util.exception.dto;

public record ErrorResponseDTO(
        String field,
        String message,
        Integer statusCode,
        String error
) {
}
