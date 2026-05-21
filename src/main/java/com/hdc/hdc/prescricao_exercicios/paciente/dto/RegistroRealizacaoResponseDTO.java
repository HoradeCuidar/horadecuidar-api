package com.hdc.hdc.prescricao_exercicios.paciente.dto;

import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
import lombok.Data;

import java.util.Date;

/**
 * Resposta após o paciente registrar ou atualizar a realização de um exercício.
 */
@Data
public class RegistroRealizacaoResponseDTO {

    private Long id;
    private Long itemExercicioId;
    private String nomeExercicio;
    private StatusRealizacao status;
    private Integer duracaoRealizadaMinutos;
    private String observacao;
    private Date dataRegistro;
}
