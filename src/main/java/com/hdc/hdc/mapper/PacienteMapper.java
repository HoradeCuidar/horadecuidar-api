package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.dto.response.PacienteResponseDto;
import com.hdc.hdc.model.Paciente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacienteMapper {

    public Paciente toEntity(PacienteCreateDto dto);

    public PacienteResponseDto toDto(Paciente entity);

    public List<PacienteResponseDto> toDto(List<Paciente> entity);
}
