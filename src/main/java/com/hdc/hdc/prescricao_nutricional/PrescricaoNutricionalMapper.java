package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalCreateDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResponseDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.RefeicaoMapper;
import com.hdc.hdc.util.formatter.DataFormatter;
import com.hdc.hdc.util.formatter.StatusPrescricaoFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
                RefeicaoMapper.class,
                StatusPrescricaoFormatter.class,
                DataFormatter.class
        }
)
public interface PrescricaoNutricionalMapper {

    @Named("createDTOtoModel")
    PrescricaoNutricional createDTOtoModel(PrescricaoNutricionalCreateDTO dto);

    @Named("modeltoResponseDTO")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusPrescricaoToString")
    @Mapping(source = "dataInicio", target = "dataInicio", qualifiedByName = "toStringDate")
    @Mapping(source = "dataFim", target = "dataFim", qualifiedByName = "toStringDate")
    @Mapping(source = "dataEncerramento", target = "dataEncerramento", qualifiedByName = "toStringDate")
    PrescricaoNutricionalResponseDTO modeltoResponseDTO(PrescricaoNutricional model);
}