package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDate;

public record DetalhamentoAdesaoMedicamentoResponseDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Page<DetalhamentoDiarioMedicamentoDTO> conteudo
) {
}
