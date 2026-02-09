package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.create.DoencaCreateDto;
import com.hdc.hdc.model.Doenca;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoencaMapper {

    public Doenca toDto(Doenca doenca);
    public Doenca toEntity(DoencaCreateDto dto);

    public List<Doenca> toDto(List<Doenca> doencas);
}
