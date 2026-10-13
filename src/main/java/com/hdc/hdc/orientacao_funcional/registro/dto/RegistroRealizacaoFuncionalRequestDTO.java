package com.hdc.hdc.orientacao_funcional.registro.dto;

import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;

public record RegistroRealizacaoFuncionalRequestDTO(
    @jakarta.validation.constraints.NotNull Long id,
    @jakarta.validation.constraints.NotNull StatusRealizacao status,
    @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(1440) Integer duracaoRealizadaMinutos,
    SensacaoFinal sensacaoFinal,
    @jakarta.validation.constraints.Size(max=5000) String observacao
) {
}
