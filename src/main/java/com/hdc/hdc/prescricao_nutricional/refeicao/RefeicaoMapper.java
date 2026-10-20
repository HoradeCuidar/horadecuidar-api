package com.hdc.hdc.prescricao_nutricional.refeicao;

import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoResponseDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoUpdateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicaoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                OpcaoRefeicaoMapper.class
        }
)
public interface RefeicaoMapper {

    @Named("createDTOtoModel")
    Refeicao createDTOtoModel(RefeicaoCreateDTO dto);

    @Named("modeltoResponseDTO")
    RefeicaoResponseDTO modeltoResponseDTO(Refeicao model);

    @Named("createDTOtoModel")
    List<Refeicao> createDTOtoModel(List<RefeicaoCreateDTO> dto);

    @Named("modeltoResponseDTO")
    List<RefeicaoResponseDTO> modeltoResponseDTO(List<Refeicao> model);

    @Named("updateDTOtoModel")
    Refeicao updateDTOtoModel(RefeicaoUpdateDTO dto);

    @Named("updateDTOtoModel")
    List<Refeicao> updateDTOtoModel(List<RefeicaoUpdateDTO> dto);
}