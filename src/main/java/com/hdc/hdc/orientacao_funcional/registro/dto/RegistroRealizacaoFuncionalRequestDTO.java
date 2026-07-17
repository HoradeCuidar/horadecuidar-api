package com.hdc.hdc.orientacao_funcional.registro.dto;

import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;

public record RegistroRealizacaoFuncionalRequestDTO(
    Long id,
    StatusRealizacao status,
    Integer duracaoRealizadaMinutos,
    SensacaoFinal sensacaoFinal,
    String observacao
) {
}
