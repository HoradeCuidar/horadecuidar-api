package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RegistroAdesaoResponseDTO {
    private Long id;
    private Long itemMedicacaoId;
    private String nomeMedicamento;
    private Integer ordemNoDia;
    private LocalDate dataPrevista;
    private StatusAdesao status;
    private String observacao;
    private LocalDateTime dataHoraRegistro;
}
