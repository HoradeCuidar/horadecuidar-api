package com.hdc.hdc.relatorio.adesao.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public record DetalhamentoAdesaoMedicamentoResponseDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Page<DetalhamentoDiarioMedicamentoDTO> conteudo
) {
}
