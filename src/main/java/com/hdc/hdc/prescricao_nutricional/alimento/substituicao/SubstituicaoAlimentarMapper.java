package com.hdc.hdc.prescricao_nutricional.alimento.substituicao;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface SubstituicaoAlimentarMapper {
    SubstituicaoAlimentar createDTOtoModel(SubstituicaoAlimentarCreateDTO dto);
    SubstituicaoAlimentar updateDTOtoModel(SubstituicaoAlimentarUpdateDTO dto);
    SubstituicaoAlimentarResponseDTO modeltoResponseDTO(SubstituicaoAlimentar model);
}
