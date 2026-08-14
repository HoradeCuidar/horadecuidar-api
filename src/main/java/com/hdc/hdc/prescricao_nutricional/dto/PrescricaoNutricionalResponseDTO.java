package com.hdc.hdc.prescricao_nutricional.dto;

import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrescricaoNutricionalResponseDTO {

    private Integer pacienteId;
    private Integer profissionalId;
    private String dataInicio;
    private String dataFim;
    private String dataEncerramento;
    private String status;
    private String observacoes;
    private List<RefeicaoResponseDTO> refeicoes;
}