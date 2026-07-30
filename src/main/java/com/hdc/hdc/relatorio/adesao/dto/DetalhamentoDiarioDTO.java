package com.hdc.hdc.relatorio.adesao.dto;

import com.hdc.hdc.relatorio.adesao.TipoOcorrencia;

import java.time.LocalDate;

public record DetalhamentoDiarioDTO(
    LocalDate data,
    TipoOcorrencia tipo,
    Integer esperado,
    Integer realizado,
    Integer naoRealizado,
    Integer semRegistro,
    Double percentual
){
}
