package com.hdc.hdc.exames;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
public record AgendamentoExame(@NotNull OffsetDateTime disponibilizacaoEm) {}
