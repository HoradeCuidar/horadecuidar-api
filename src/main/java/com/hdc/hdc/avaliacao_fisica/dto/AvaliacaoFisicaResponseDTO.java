package com.hdc.hdc.avaliacao_fisica.dto;

import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.avaliacao_fisica.enums.FlexibilidadeFisica;
import com.hdc.hdc.avaliacao_fisica.enums.NivelAssimetria;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

public record AvaliacaoFisicaResponseDTO (
        UUID id,
        PacienteResponseDto paciente,
        ProfissionalDaSaudeResponseDTO profissional,
        boolean realizaAtividadeFisica,
        String atividadeRealizada,
        Integer frequenciaSemanal,
        FlexibilidadeFisica flexibilidade,
        String forcaPalmarDireita,
        String forcaPalmarEsquerda,
        NivelAssimetria assimetriaPalmar,
        String forcaJoelhoDireita,
        String forcaJoelhoEsquerda,
        NivelAssimetria assimetriaJoelho,
        String queixas,
        String observacoesMusculoEsqueleticas,
        String orientacoesGerais,
        LocalDate dataRegistro,
        LocalDate dataAtualizacao
) {
}
