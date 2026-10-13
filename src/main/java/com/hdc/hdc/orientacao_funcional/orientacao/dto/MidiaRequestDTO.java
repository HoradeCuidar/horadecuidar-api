package com.hdc.hdc.orientacao_funcional.orientacao.dto;
import jakarta.validation.constraints.*;
public record MidiaRequestDTO(Long passoId, @Min(0) @Max(9) int ordem,
 @Size(max=500) String legenda, @Size(max=500) String textoAlternativo, boolean capa) {}
