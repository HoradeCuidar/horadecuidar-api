package com.hdc.hdc.prescricao_medicamentos;

import com.hdc.hdc.medicamentos.Medicamento;
import com.hdc.hdc.medicamentos.MedicamentoService;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PrescricaoMedicamentoMapper {

    private final MedicamentoService medicamentoService;

    public ItemMedicacao toItemMedicacaoEntity(ItemMedicacaoDTO itemDto, PrescricaoMedicamento prescricao) {
        ItemMedicacao item = new ItemMedicacao();
        item.setPrescricao(prescricao);

        Medicamento medicamento = medicamentoService.buscarOuCriar(itemDto.getNomeMedicamento());
        item.setMedicamento(medicamento);
        item.setNomeMedicamento(medicamento.getNome());

        item.setDosagemValor(itemDto.getDosagemValor());
        item.setDosagemUnidade(itemDto.getDosagemUnidade());
        item.setQuantidadeDoses(itemDto.getQuantidadeDoses());
        item.setIntervaloValor(itemDto.getIntervaloValor());
        item.setIntervaloTipo(itemDto.getIntervaloTipo());
        item.setViaAdministracao(itemDto.getViaAdministracao());
        item.setObservacao(itemDto.getObservacao());
        return item;
    }

    public ItemMedicacao toItemMedicacaoEntity(String nome, PrescricaoMedicamento prescricao) {
        ItemMedicacao item = new ItemMedicacao();
        item.setPrescricao(prescricao);
        Medicamento medicamento = medicamentoService.buscarOuCriar(nome);
        item.setMedicamento(medicamento);
        item.setNomeMedicamento(medicamento.getNome());
        return item;
    }

    public PrescricaoMedicamentoResponseDTO toResponseDTO(PrescricaoMedicamento prescricao) {
        PrescricaoMedicamentoResponseDTO dto = new PrescricaoMedicamentoResponseDTO();
        dto.setId(prescricao.getId());
        dto.setPacienteId(this.toUsuarioDTO(prescricao.getPaciente()));
        if (prescricao.getProfissional() != null) {
            dto.setProfissionalId(this.toUsuarioDTO(prescricao.getProfissional()));
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
        itemDto.setAtivo(item.isAtivo());
        return itemDto;
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
