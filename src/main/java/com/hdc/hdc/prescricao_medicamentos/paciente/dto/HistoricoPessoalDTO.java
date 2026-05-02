package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HistoricoPessoalDTO {
    private String periodo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int totalItensEsperados;
    private int totalItensRealizados;
    private int totalItensNaoRealizados;
    private double percentualAdesao;
}
