package com.hdc.hdc.prescricao_exercicios.paciente.dto;

import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
import lombok.Data;

import java.util.Date;

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
