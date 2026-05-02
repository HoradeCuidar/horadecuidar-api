package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistroAdesaoRequestDTO {

    @NotNull(message = "O ID do item de medicação é obrigatório.")
    private Long itemMedicacaoId;

    @NotNull(message = "O status da adesão é obrigatório.")
    private StatusAdesao status;

    private String observacao;
}
