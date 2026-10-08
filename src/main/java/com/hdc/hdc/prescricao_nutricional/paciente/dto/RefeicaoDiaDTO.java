package com.hdc.hdc.prescricao_nutricional.paciente.dto;

import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.dto.OcorrenciaRefeicaoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record RefeicaoDiaDTO(
        LocalDate data,
        List<OcorrenciaRefeicaoResponseDTO> ocorrencias
) {
}
