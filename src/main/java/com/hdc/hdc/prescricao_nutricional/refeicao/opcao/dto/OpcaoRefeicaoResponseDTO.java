package com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto;

import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpcaoRefeicaoResponseDTO {

    private Integer ordem;
    private String descricao;
    private List<AlimentoPrescritoResponseDTO> alimentos;
}