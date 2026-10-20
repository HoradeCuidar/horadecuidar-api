package com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto;

import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class OpcaoRefeicaoUpdateDTO {

    @Min(value = 1, message = "A ordem deve ser maior que zero")
    private Integer ordem;

    private String descricao;

    @Valid
    @NotEmpty(message = "A opção deve possuir pelo menos um alimento")
    private List<AlimentoPrescritoUpdateDTO> alimentos;
}