package com.hdc.hdc.doencas;

import com.hdc.hdc.doencas.dto.DoencaCreateDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoencaMapper {

    public Doenca toDto(Doenca doenca);
    public Doenca toEntity(DoencaCreateDto dto);

    public List<Doenca> toDto(List<Doenca> doencas);
}
