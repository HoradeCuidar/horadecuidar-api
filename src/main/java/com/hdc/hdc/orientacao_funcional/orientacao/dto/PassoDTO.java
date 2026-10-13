package com.hdc.hdc.orientacao_funcional.orientacao.dto;
import jakarta.validation.constraints.Size;
public record PassoDTO(Long id, @Size(max=5000) String instrucao) {}
