package com.hdc.hdc.prescricao_nutricional.refeicao.dto;

import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefeicaoResponseDTO {

    private Integer id;
    private String nome;
    private Integer ordem;
    private String observacoes;
    private LocalTime horario;
    private List<OpcaoRefeicaoResponseDTO> opcoes;
}