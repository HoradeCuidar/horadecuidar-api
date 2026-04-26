package com.hdc.hdc.medicamentos;

import com.hdc.hdc.medicamentos.dto.MedicamentoDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicamentoMapper {

    MedicamentoDTO toDTO(Medicamento medicamento);

    List<MedicamentoDTO> toDTOList(List<Medicamento> medicamentos);
}
