package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.dto.OcorrenciaMedicamentoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record ItemMedicacaoDiaDTO (
    LocalDate data,
    List<OcorrenciaMedicamentoResponseDTO> ocorrencias
) {
}

//Long id,
//PrescricaoMedicamentoResponseDTO prescricao,
//ItemMedicacaoDTO itemMedicacao,
//LocalDate dataPrevista,
//Integer ordemNoDia,
//LocalDateTime dataHoraRegistro,
//String observacao