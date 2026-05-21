package com.hdc.hdc.prescricao_exercicios.dto;

import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicioDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * DTO de entrada para criar ou atualizar uma prescrição de exercícios.
 */
@Data
public class PrescricaoExercicioRequestDTO {

    @NotNull(message = "A data de início é obrigatória.")
    private Date dataInicio;

    private Date dataFim;

    private String observacao;

    @NotEmpty(message = "A prescrição deve conter ao menos um exercício.")
    @Valid
    private List<ItemExercicioDTO> exercicios;
}
