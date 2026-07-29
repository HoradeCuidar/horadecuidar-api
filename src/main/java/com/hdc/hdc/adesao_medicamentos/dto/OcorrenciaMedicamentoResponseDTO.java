package com.hdc.hdc.adesao_medicamentos.dto;

import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OcorrenciaMedicamentoResponseDTO(
        Long id,
        UUID prescricaoId,
        ItemMedicacaoDTO itemMedicacao,
        LocalDate dataPrevista,
        Integer ordemNoDia,
        LocalDateTime dataHoraRegistro,
        String observacao
) {
}
