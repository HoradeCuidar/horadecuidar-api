package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.MedicamentoDTO;
import com.hdc.hdc.model.Medicamento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicamentoMapper {

    MedicamentoDTO toDTO(Medicamento medicamento);

    List<MedicamentoDTO> toDTOList(List<Medicamento> medicamentos);
}
