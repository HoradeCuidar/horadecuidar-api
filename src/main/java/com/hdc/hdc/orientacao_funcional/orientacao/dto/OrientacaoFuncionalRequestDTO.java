package com.hdc.hdc.orientacao_funcional.orientacao.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record OrientacaoFuncionalRequestDTO(
        @NotBlank(message = "A identificação do exercício deve ser informada.")
        String nome,

        String descricao,

        String finalidade,

        List<Long> tagsIds
) {
}

