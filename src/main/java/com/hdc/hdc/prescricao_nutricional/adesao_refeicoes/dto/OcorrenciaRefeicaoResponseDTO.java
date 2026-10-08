package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.dto;

import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoResponseDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OcorrenciaRefeicaoResponseDTO(
        Integer id,
        Integer prescricaoId,
        StatusAdesao status,
        RefeicaoResponseDTO refeicao,
        OpcaoRefeicaoResponseDTO opcao,
        LocalDate dataPrevista,
        Integer ordemNoDia,
        LocalDateTime dataHoraRegistro,
        String observacao
) {
}
