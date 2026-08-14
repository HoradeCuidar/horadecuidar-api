package com.hdc.hdc.prescricao_nutricional.alimento;

import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoResponseDTO;
import com.hdc.hdc.util.formatter.UnidadeMedidaFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                UnidadeMedidaFormatter.class
        }
)
public interface AlimentoPrescritoMapper {

    @Named("createDTOtoModel")
    AlimentoPrescrito createDTOtoModel(AlimentoPrescritoCreateDTO dto);

    @Named("modeltoResponseDTO")
    @Mapping(source = "unidade", target = "unidade", qualifiedByName = "unidadeMedidaToString")
    AlimentoPrescritoResponseDTO modeltoResponseDTO(AlimentoPrescrito model);

    @Named("createDTOtoModel")
    List<AlimentoPrescrito> createDTOtoModel(List<AlimentoPrescritoCreateDTO> dto);

    @Named("modeltoResponseDTO")
    @Mapping(source = "unidade", target = "unidade", qualifiedByName = "unidadeMedidaToString")
    List<AlimentoPrescritoResponseDTO> modeltoResponseDTO(List<AlimentoPrescrito> model);
}