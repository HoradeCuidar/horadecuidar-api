package com.hdc.hdc.prescricao_nutricional.alimento.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlimentoPrescritoResponseDTO {

    private Integer id;
    private String descricao;
    private BigDecimal quantidade;
    private String unidade;
    private String observacao;
}