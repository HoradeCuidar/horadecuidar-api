package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.create.ProfissionalDaSaudeCreateDTO;
import com.hdc.hdc.dto.response.ProfissionalDaSaudeResponseDTO;
import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.util.DataFormatter;
import com.hdc.hdc.util.GeneroFormatter;
import com.hdc.hdc.util.StatusFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { DataFormatter.class, GeneroFormatter.class, StatusFormatter.class})
public interface ProfissionalDaSaudeMapper {

    @Named("createDTOtoModel")
    ProfissionalDaSaude createDTOtoModel(ProfissionalDaSaudeCreateDTO profissionalDaSaudeCreateDTO);

    @Named("modeltoResponseDTO")
    @Mapping(source = "genero", target = "genero", qualifiedByName = "generoToString")
    @Mapping(source = "dataDeNascimento", target = "dataDeNascimento", qualifiedByName = "toStringDate")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    ProfissionalDaSaudeResponseDTO modeltoResponseDTO(ProfissionalDaSaude profissionalDaSaude);
}