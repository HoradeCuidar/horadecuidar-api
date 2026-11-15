package com.hdc.hdc.prescricao_nutricional.modelo;

import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class ModeloNutricionalRequestDTO {
    @NotBlank @Size(max = 160)
    private String nome;
    @Min(1)
    private Integer referenciaKcal;
    private String descricao;
    private Integer versao;
    @Valid @NotEmpty
    private List<RefeicaoCreateDTO> refeicoes;
}
