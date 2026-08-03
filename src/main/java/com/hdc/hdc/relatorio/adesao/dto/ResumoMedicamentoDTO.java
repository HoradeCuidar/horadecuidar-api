package com.hdc.hdc.relatorio.adesao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumoMedicamentoDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Integer esperado,
        Integer realizado,
        Integer naoRealizado,
        Integer semRegistro,
        BigDecimal percentual
) {
}
