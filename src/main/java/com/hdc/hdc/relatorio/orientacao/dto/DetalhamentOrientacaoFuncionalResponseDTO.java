package com.hdc.hdc.relatorio.orientacao.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDate;

public record DetalhamentOrientacaoFuncionalResponseDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Page<DetalhamentoDiarioFuncionalDTO> conteudo
){
}
