package com.hdc.hdc.prescricao_nutricional.refeicao.opcao;

import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescritoMapper;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                AlimentoPrescritoMapper.class
        }
)
public interface OpcaoRefeicaoMapper {

    @Named("createDTOtoModel")
    OpcaoRefeicao createDTOtoModel(OpcaoRefeicaoCreateDTO dto);

    @Named("modeltoResponseDTO")
    OpcaoRefeicaoResponseDTO modeltoResponseDTO(OpcaoRefeicao model);

    @Named("createDTOtoModel")
    List<OpcaoRefeicao> createDTOtoModel(List<OpcaoRefeicaoCreateDTO> dto);

    @Named("modeltoResponseDTO")
    List<OpcaoRefeicaoResponseDTO> modeltoResponseDTO(List<OpcaoRefeicao> model);
}