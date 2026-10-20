package com.hdc.hdc.indicadores.relatorio.adesao.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDate;

public record DetalhamentoAdesaoNutricionalResponseDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Page<DetalhamentoDiarioNutricionalDTO> conteudo
) {
}
