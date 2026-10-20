package com.hdc.hdc.prescricao_nutricional.paciente.dto;

import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RegistroAdesaoNutricionalResponseDTO {
    private Integer id;
    private Integer prescricaoId;
    private Integer refeicaoId;
    private String nomeRefeicao;
    private Integer opcaoId;
    private Integer ordemNoDia;
    private LocalDate dataPrevista;
    private StatusAdesao status;
    private String observacao;
    private LocalDateTime dataHoraRegistro;
}
