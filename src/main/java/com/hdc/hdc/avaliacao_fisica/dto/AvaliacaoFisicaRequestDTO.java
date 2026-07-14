package com.hdc.hdc.avaliacao_fisica.dto;

import com.hdc.hdc.avaliacao_fisica.enums.FlexibilidadeFisica;
import com.hdc.hdc.avaliacao_fisica.enums.NivelAssimetria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoFisicaRequestDTO(
        @NotNull(message = "A realização de atividades físicas do participante deve ser informada.")
        boolean realizaAtividadeFisica,
        String atividadeRealizada,
        Integer frequenciaSemanal,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "O nível de flexibilidade do participante deve ser informada.")
        FlexibilidadeFisica flexibilidade,

        String forcaPalmarDireita,
        String forcaPalmarEsquerda,
        NivelAssimetria assimetriaPalmar,

        String forcaJoelhoDireita,
        String forcaJoelhoEsquerda,
        NivelAssimetria assimetriaJoelho,

        String queixas,
        String observacoesMusculoEsqueleticas,
        String orientacoesGerais
) {
}
