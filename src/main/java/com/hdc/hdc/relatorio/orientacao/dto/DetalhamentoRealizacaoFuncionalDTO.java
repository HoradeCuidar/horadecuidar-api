package com.hdc.hdc.relatorio.orientacao.dto;

import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;

import java.time.LocalDate;

public record DetalhamentoRealizacaoFuncionalDTO(
        LocalDate data,
        String nomeOrientacao,
        Long idOrientacao,
        StatusRealizacao status,
        Integer duracao,
        SensacaoFinal sensacaoFinal
) {
}
