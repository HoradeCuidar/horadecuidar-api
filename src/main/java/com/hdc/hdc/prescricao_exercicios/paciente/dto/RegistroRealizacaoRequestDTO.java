package com.hdc.hdc.prescricao_exercicios.paciente.dto;

import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Requisição para o paciente registrar a realização (ou não) de um exercício do dia.
 */
@Data
public class RegistroRealizacaoRequestDTO {

    @NotNull(message = "O ID do item de exercício é obrigatório.")
    private Long itemExercicioId;

    @NotNull(message = "O status de realização é obrigatório.")
    private StatusRealizacao status;

    /**
     * Duração real da sessão em minutos — opcional.
     * Permite registrar quando o paciente fez menos ou mais do que o prescrito.
     */
    private Integer duracaoRealizadaMinutos;

    private String observacao;
}
