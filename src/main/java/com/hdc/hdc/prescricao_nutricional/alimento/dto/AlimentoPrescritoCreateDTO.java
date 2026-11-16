package com.hdc.hdc.prescricao_nutricional.alimento.dto;

import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import com.hdc.hdc.prescricao_nutricional.alimento.substituicao.SubstituicaoAlimentarCreateDTO;
import jakarta.validation.Valid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoPrescritoCreateDTO {

    @NotBlank(message = "Descrição do alimento é obrigatória")
    private String descricao;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotNull(message = "Unidade é obrigatória")
    private UnidadeDeMedida unidade;

    private String observacao;
    private String medidaCaseira;
    @Valid private List<SubstituicaoAlimentarCreateDTO> substituicoes;
}