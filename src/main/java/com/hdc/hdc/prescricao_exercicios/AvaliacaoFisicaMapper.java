package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import org.springframework.data.domain.Page;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AvaliacaoFisicaMapper {

    public abstract AvaliacaoFisicaResponseDTO toResponseDTO(AvaliacaoFisica avaliacao);
    public abstract Page<AvaliacaoFisicaResponseDTO> toResponseDTO(Page<AvaliacaoFisica> avaliacao);
    public abstract AvaliacaoFisica toEntity(AvaliacaoFisicaRequestDTO avaliacao, Integer idPaciente, Usuario profissional);
}
