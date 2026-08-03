package com.hdc.hdc.relatorio.orientacao.dto;

import org.springframework.data.domain.Page;

import java.time.LocalDate;

public record DetalhamentoOrientacaoFuncionalResponseDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Page<DetalhamentoRealizacaoFuncionalDTO> conteudo
){
}
