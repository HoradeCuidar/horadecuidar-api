package com.hdc.hdc.avaliacao_fisica.dto;

import com.hdc.hdc.avaliacao_fisica.enums.FlexibilidadeFisica;
import com.hdc.hdc.avaliacao_fisica.enums.NivelAssimetria;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalDTO;
import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AvaliacaoFisicaResponseDTO (
        Long id,
        PacienteResponseDto paciente,
        ProfissionalDaSaudeResponseDTO profissional,
        boolean realizaAtividadeFisica,
        String atividadeRealizada,
        Integer frequenciaSemanal,
        FlexibilidadeFisica flexibilidade,
        BigDecimal forcaPalmarDireita,
        BigDecimal forcaPalmarEsquerda,
        NivelAssimetria assimetriaPalmar,
        BigDecimal forcaJoelhoDireita,
        BigDecimal forcaJoelhoEsquerda,
        NivelAssimetria assimetriaJoelho,
        String queixas,
        String observacoesMusculoEsqueleticas,
        String orientacoesGerais,
        List<TagFuncionalDTO> indicacoesFuncionais,
        LocalDateTime dataRegistro,
        LocalDateTime dataAtualizacao
) {
}
