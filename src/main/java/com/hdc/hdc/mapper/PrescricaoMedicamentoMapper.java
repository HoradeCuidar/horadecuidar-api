package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.ItemMedicacaoDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.model.PrescricaoMedicamento;
import com.hdc.hdc.model.associacoes.ItemMedicacao;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public class PrescricaoMedicamentoMapper {

    public ItemMedicacao toItemMedicacaoEntity(ItemMedicacaoDTO itemDto, PrescricaoMedicamento prescricao) {
        ItemMedicacao item = new ItemMedicacao();
        item.setPrescricao(prescricao);
        item.setNomeMedicamento(itemDto.getNomeMedicamento());
        item.setDosagemValor(itemDto.getDosagemValor());
        item.setDosagemUnidade(itemDto.getDosagemUnidade());
        item.setQuantidadeDoses(itemDto.getQuantidadeDoses());
        item.setIntervaloValor(itemDto.getIntervaloValor());
        item.setIntervaloTipo(itemDto.getIntervaloTipo());
        item.setViaAdministracao(itemDto.getViaAdministracao());
        item.setObservacao(itemDto.getObservacao());
        return item;
    }

    public PrescricaoMedicamentoResponseDTO toResponseDTO(PrescricaoMedicamento prescricao) {
        PrescricaoMedicamentoResponseDTO dto = new PrescricaoMedicamentoResponseDTO();
        dto.setId(prescricao.getId());
        dto.setPacienteId(prescricao.getPaciente().getId());
        if (prescricao.getProfissional() != null) {
            dto.setProfissionalId(prescricao.getProfissional().getId());
            dto.setNomeProfissional(prescricao.getProfissional().getNome());
        }
        dto.setDataInicio(prescricao.getDataInicio());
        dto.setDataFim(prescricao.getDataFim());
        dto.setObservacao(prescricao.getObservacao());
        dto.setAtivo(prescricao.isAtivo());

        if (prescricao.getMedicacoes() != null) {
            List<ItemMedicacaoDTO> itensDto = prescricao.getMedicacoes().stream()
                    .map(this::toItemMedicacaoDTO)
                    .toList();
            dto.setMedicacoes(itensDto);
        }

        return dto;
    }

    public ItemMedicacaoDTO toItemMedicacaoDTO(ItemMedicacao item) {
        ItemMedicacaoDTO itemDto = new ItemMedicacaoDTO();
        itemDto.setId(item.getId());
        itemDto.setNomeMedicamento(item.getNomeMedicamento());
        itemDto.setDosagemValor(item.getDosagemValor());
        itemDto.setDosagemUnidade(item.getDosagemUnidade());
        itemDto.setQuantidadeDoses(item.getQuantidadeDoses());
        itemDto.setIntervaloValor(item.getIntervaloValor());
        itemDto.setIntervaloTipo(item.getIntervaloTipo());
        itemDto.setViaAdministracao(item.getViaAdministracao());
        itemDto.setObservacao(item.getObservacao());
        return itemDto;
    }
}
