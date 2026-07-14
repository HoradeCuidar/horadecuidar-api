package com.hdc.hdc.orientacao_exercicio.dto;

import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
import java.util.UUID;

public record ExercicioOrientacaoResponseDTO(
    UUID id,
    String nome,
    String finalidade,
    CategoriaExercicio categoria,
    String descricao,
    String urlImagem
) {}
