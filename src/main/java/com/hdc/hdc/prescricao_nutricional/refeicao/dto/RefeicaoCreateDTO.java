package com.hdc.hdc.prescricao_nutricional.refeicao.dto;

import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefeicaoCreateDTO {

    @NotBlank(message = "Nome da refeição é obrigatório")
    private String nome;

    @Min(value = 1, message = "A ordem deve ser maior que zero")
    private Integer ordem;

    private String observacoes;

    @Valid
    @NotEmpty(message = "A refeição deve possuir pelo menos uma opção")
    private List<OpcaoRefeicaoCreateDTO> opcoes;
}