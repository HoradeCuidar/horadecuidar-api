package com.hdc.hdc.orientacao_funcional.orientacao;

import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalRequestDTO;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrientacaoFuncionalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "urlImagem", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    public abstract OrientacaoFuncional toEntity(OrientacaoFuncionalRequestDTO requestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "urlImagem", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    public abstract void updateEntityFromDto(OrientacaoFuncionalRequestDTO requestDTO, @MappingTarget OrientacaoFuncional entity);

    public abstract OrientacaoFuncionalResponseDTO toDto(OrientacaoFuncional entity);
}

