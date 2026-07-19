package com.hdc.hdc.orientacao_funcional.tag;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagFuncionalMapper {

    TagFuncional toEntity(TagFuncionalCreateDto dto);

    TagFuncionalDTO toDTO(TagFuncional tagFuncional);

}
