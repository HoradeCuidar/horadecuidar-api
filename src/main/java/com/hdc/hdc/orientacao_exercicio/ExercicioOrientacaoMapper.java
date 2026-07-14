package com.hdc.hdc.orientacao_exercicio;

import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoRequestDTO;
import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExercicioOrientacaoMapper {

    ExercicioOrientacao toEntity(ExercicioOrientacaoRequestDTO dto);

    ExercicioOrientacaoResponseDTO toResponseDTO(ExercicioOrientacao exercicio);
}
