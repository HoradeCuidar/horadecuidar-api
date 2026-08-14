package com.hdc.hdc.orientacao_funcional.registro.dto;

import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;

import java.time.LocalDateTime;

public record RegistroRealizacaoFuncionalResponseDTO(
        Long id,
        Long orientacaoFuncionalId,
        String nomeOrientacao,
        StatusRealizacao status,
        Integer duracaoRealizadaMinutos,
        SensacaoFinal sensacaoFinal,
        String observacao,
        LocalDateTime dataRegistro
) {
}
