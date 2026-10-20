package com.hdc.hdc.exames;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DadosExame(@NotNull LocalDate dataColeta,
                         @Size(max = 200) String laboratorio,
                         String observacao) {}
