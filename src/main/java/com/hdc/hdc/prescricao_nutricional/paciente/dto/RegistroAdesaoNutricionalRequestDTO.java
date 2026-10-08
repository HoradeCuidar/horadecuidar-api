package com.hdc.hdc.prescricao_nutricional.paciente.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistroAdesaoNutricionalRequestDTO {

    @NotNull(message = "O ID da ocorrência é obrigatório.")
    private Integer ocorrenciaId;

    @NotNull(message = "O ID da opção da refeição é obrigatório.")
    private Integer opcaoId;

    private String observacao;
}
