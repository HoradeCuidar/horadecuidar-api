package com.hdc.hdc.prescricao_nutricional.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescricaoNutricionalResumoDTO {

    private Integer id;
    private Integer pacienteId;
    private Integer profissionalId;
    private String dataInicio;
    private String dataFim;
    private String dataEncerramento;
    private String status;
    private String observacoes;
}