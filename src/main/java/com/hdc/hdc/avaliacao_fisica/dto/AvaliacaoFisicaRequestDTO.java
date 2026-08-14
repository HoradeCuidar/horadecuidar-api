package com.hdc.hdc.avaliacao_fisica.dto;

import com.hdc.hdc.avaliacao_fisica.enums.FlexibilidadeFisica;
import com.hdc.hdc.avaliacao_fisica.enums.NivelAssimetria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record AvaliacaoFisicaRequestDTO(
        @NotNull(message = "A realização de atividades físicas do participante deve ser informada.")
        boolean realizaAtividadeFisica,
        String atividadeRealizada,
        Integer frequenciaSemanal,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "O nível de flexibilidade do participante deve ser informada.")
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

        List<Long> indicacoesFuncionaisIds
) {
}

