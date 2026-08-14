package com.hdc.hdc.orientacao_funcional.orientacao.dto;

import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalDTO;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record OrientacaoFuncionalResponseDTO(
        Long id,
        ProfissionalDaSaudeResponseDTO responsavel,
        String nome,
        String descricao,
        String finalidade,
        String urlImagem,
        Boolean ativo,
        List<TagFuncionalDTO> tags,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}


