package com.hdc.hdc.orientacao_funcional.orientacao.dto;
import com.hdc.hdc.orientacao_funcional.orientacao.TipoCuidado;
import jakarta.validation.constraints.*;
public record CuidadoDTO(@NotNull TipoCuidado tipo, @Size(max=5000) String texto) {}
