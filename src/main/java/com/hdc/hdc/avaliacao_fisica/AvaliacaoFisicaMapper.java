package com.hdc.hdc.avaliacao_fisica;

import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AvaliacaoFisicaMapper {

    public abstract AvaliacaoFisicaResponseDTO toResponseDTO(AvaliacaoFisica avaliacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "dataRegistro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "indicacoesFuncionais", ignore = true)
    public abstract AvaliacaoFisica toEntity(AvaliacaoFisicaRequestDTO avaliacao, Integer idPaciente, Usuario profissional);
}


