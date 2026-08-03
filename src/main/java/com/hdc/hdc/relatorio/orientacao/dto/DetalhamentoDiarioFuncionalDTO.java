package com.hdc.hdc.relatorio.orientacao.dto;

import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalResponseDTO;
import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;

import java.time.LocalDate;

public record DetalhamentoDiarioFuncionalDTO(
        LocalDate data,
        OrientacaoFuncionalResponseDTO orientacao,
        StatusRealizacao status,
        Integer duracao,
        SensacaoFinal sensacaoFinal
) {
}
