package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroAdesaoRequestDTO {

    @NotNull(message = "O ID da ocorrência é obrigatório.")
    private Long ocorrenciaId;

    @NotNull(message = "O ID da Item de Medicação é obrigatório.")
    private Long itemMedicacaoId;

    private Integer ordemNoDia;

    private LocalDate dataPrevista;

    private Integer quantidadeDiaria;

    @NotNull(message = "O status da adesão é obrigatório.")
    private StatusAdesao status;

    private String observacao;
}
