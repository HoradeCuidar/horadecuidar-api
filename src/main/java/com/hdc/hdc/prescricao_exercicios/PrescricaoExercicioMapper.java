package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicio;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicioDTO;
import com.hdc.hdc.prescricao_exercicios.dto.PrescricaoExercicioResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper responsável pelas conversões entre entidades e DTOs do módulo de prescrições de exercícios.
 */
@Component
@RequiredArgsConstructor
public class PrescricaoExercicioMapper {

    public ItemExercicio toItemExercicioEntity(ItemExercicioDTO dto, PrescricaoExercicio prescricao) {
        ItemExercicio item = new ItemExercicio();
        item.setPrescricao(prescricao);
        item.setNomeExercicio(dto.getNomeExercicio());
        item.setTipoExercicio(dto.getTipoExercicio());
        item.setFrequenciaValor(dto.getFrequenciaValor());
        item.setFrequenciaTipo(dto.getFrequenciaTipo());
        item.setDuracaoValor(dto.getDuracaoValor());
        item.setUnidadeDuracao(dto.getUnidadeDuracao());
        item.setSeries(dto.getSeries());
        item.setRepeticoes(dto.getRepeticoes());
        item.setIntensidade(dto.getIntensidade());
        item.setObservacao(dto.getObservacao());
        return item;
    }

    public ItemExercicioDTO toItemExercicioDTO(ItemExercicio item) {
        ItemExercicioDTO dto = new ItemExercicioDTO();
        dto.setNomeExercicio(item.getNomeExercicio());
        dto.setTipoExercicio(item.getTipoExercicio());
        dto.setFrequenciaValor(item.getFrequenciaValor());
        dto.setFrequenciaTipo(item.getFrequenciaTipo());
        dto.setDuracaoValor(item.getDuracaoValor());
        dto.setUnidadeDuracao(item.getUnidadeDuracao());
        dto.setSeries(item.getSeries());
        dto.setRepeticoes(item.getRepeticoes());
        dto.setIntensidade(item.getIntensidade());
        dto.setObservacao(item.getObservacao());
        return dto;
    }

    public PrescricaoExercicioResponseDTO toResponseDTO(PrescricaoExercicio prescricao) {
        PrescricaoExercicioResponseDTO dto = new PrescricaoExercicioResponseDTO();
        dto.setId(prescricao.getId());
        dto.setPaciente(toUsuarioDTO(prescricao.getPaciente()));

        if (prescricao.getProfissional() != null) {
            dto.setProfissional(toUsuarioDTO(prescricao.getProfissional()));
            dto.setNomeProfissional(prescricao.getProfissional().getNome());
        }

        dto.setDataInicio(prescricao.getDataInicio());
        dto.setDataFim(prescricao.getDataFim());
        dto.setObservacao(prescricao.getObservacao());
        dto.setAtivo(prescricao.isAtivo());

        if (prescricao.getExercicios() != null) {
            List<ItemExercicioDTO> itensDto = prescricao.getExercicios().stream()
                    .map(this::toItemExercicioDTO)
                    .toList();
            dto.setExercicios(itensDto);
        }

        return dto;
    }

    private UsuarioDTO toUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getRole(),
                usuario.getStatus(),
                usuario.getTelefone(),
                usuario.getGenero(),
                usuario.getEmail(),
                usuario.getFotoDePerfil()
        );
    }
}
