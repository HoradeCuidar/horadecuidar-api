package com.hdc.hdc.adesao_medicamentos;

import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import com.hdc.hdc.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcorrenciaMedicamentoValidationService {

    private final OcorrenciaMedicamentoRepository ocorrenciaMedicamentoRepository;

    public void geradorOcorrencias(PrescricaoMedicamento prescricao) {
        for (ItemMedicacao item : prescricao.getMedicacoes()) {
            if (!item.isAtivo()) continue;
            for (
                    LocalDate data = prescricao.getDataInicio();
                    !data.isAfter(prescricao.getDataFim());
                    data = data.plusDays(1)
            ) {
                for (
                        int ordem = 1;
                        ordem <= item.getQuantidadeDoses();
                        ordem++
                ) {
                    boolean existe = false;
                    if (item.getId() != null) {
                        existe = ocorrenciaMedicamentoRepository
                                .existsByItemMedicacaoIdAndDataPrevistaAndOrdemNoDiaAndStatusNot(
                                        item.getId(), data, ordem, StatusAdesao.CANCELADO);
                    }
                    if (!existe) {
                        criarOcorrencia(item, data, ordem);
                    }
                }
            }
        }
    }

    private void criarOcorrencia(ItemMedicacao item, LocalDate data, int ordem) {
        OcorrenciaMedicamento ocorrencia = new OcorrenciaMedicamento();
        ocorrencia.setPrescricao(item.getPrescricao());
        ocorrencia.setItemMedicacao(item);
        ocorrencia.setDataPrevista(data);
        ocorrencia.setOrdemNoDia(ordem);
        ocorrencia.setStatus(StatusAdesao.PENDENTE);

        ocorrenciaMedicamentoRepository.save(ocorrencia);
    }

    public void cancelarOcorrencias(PrescricaoMedicamento prescricao) {
        List<OcorrenciaMedicamento> ocorrencias = ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId());

        for (OcorrenciaMedicamento ocorrencia : ocorrencias) {
            if(ocorrencia.getStatus() == StatusAdesao.PENDENTE &&
                    (ocorrencia.getDataPrevista().isBefore(prescricao.getDataFim()) && ocorrencia.getDataPrevista().isAfter(LocalDate.now()))) {
                ocorrencia.setStatus(StatusAdesao.CANCELADO);
                ocorrenciaMedicamentoRepository.save(ocorrencia);
            }
        }
    }

    public void reativarOcorrencias(PrescricaoMedicamento prescricao) {
        List<OcorrenciaMedicamento> ocorrencias = ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId());

        for (OcorrenciaMedicamento ocorrencia : ocorrencias) {
            if(ocorrencia.getStatus() == StatusAdesao.CANCELADO &&
                    (ocorrencia.getDataPrevista().isBefore(prescricao.getDataFim()) && ocorrencia.getDataPrevista().isAfter(LocalDate.now()))) {
                ocorrencia.setStatus(StatusAdesao.PENDENTE);
                ocorrenciaMedicamentoRepository.save(ocorrencia);
            }
        }
    }

    public void cancelarOcorrenciasFuturasPendentes(
            UUID prescricaoId,
            LocalDate hoje
    ) {
        List<OcorrenciaMedicamento> ocorrencias =
                ocorrenciaMedicamentoRepository.buscarOcorrenciasFuturas(
                        prescricaoId,
                        hoje,
                        StatusAdesao.PENDENTE
                );

        ocorrencias.forEach(OcorrenciaMedicamento::cancelar);
    }

    public void atualizarDadosDaPrescricao(
            PrescricaoMedicamento prescricao,
            PrescricaoMedicamentoRequestDTO request,
            Usuario profissional
    ) {
        prescricao.setDataInicio(request.getDataInicio());
        prescricao.setDataFim(request.getDataFim());
        prescricao.setProfissional(profissional);
        prescricao.setObservacao(request.getObservacao());
    }

    public void validarPeriodo(
            LocalDate dataInicio,
            LocalDate dataFim
    ) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException(
                    "As datas inicial e final são obrigatórias."
            );
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }
    }

    public void sincronizarItens(
            PrescricaoMedicamento prescricao,
            List<ItemMedicacaoDTO> itensRequest
    ) {
        if (itensRequest == null || itensRequest.isEmpty())
            throw new IllegalArgumentException("A prescrição deve possuir pelo menos um medicamento.");

        Map<Long, ItemMedicacao> itensExistentes =
                prescricao.getMedicacoes()
                        .stream()
                        .filter(item -> item.getId() != null)
                        .collect(Collectors.toMap(
                                ItemMedicacao::getId,
                                Function.identity()
                        ));

        Set<Long> idsRecebidos = itensRequest.stream()
                .map(ItemMedicacaoDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        validarItensPertencentesAPrescricao(
                itensExistentes,
                idsRecebidos
        );

        inativarItensAusentes(
                prescricao,
                idsRecebidos
        );

        for (ItemMedicacaoDTO itemRequest : itensRequest) {
            if (itemRequest.getId() == null) {
                adicionarNovoItem(prescricao, itemRequest);
                continue;
            }

            ItemMedicacao itemExistente =
                    itensExistentes.get(itemRequest.getId());

            atualizarItem(itemExistente, itemRequest);
        }
    }

    private void validarItensPertencentesAPrescricao(
            Map<Long, ItemMedicacao> itensExistentes,
            Set<Long> idsRecebidos
    ) {
        boolean possuiIdInvalido = idsRecebidos.stream()
                .anyMatch(id -> !itensExistentes.containsKey(id));

        if (possuiIdInvalido) {
            throw new IllegalArgumentException("Um dos medicamentos informados não pertence à prescrição.");
        }
    }

    private void inativarItensAusentes(
            PrescricaoMedicamento prescricao,
            Set<Long> idsRecebidos
    ) {
        prescricao.getMedicacoes().forEach(item -> {
            if (item.getId() != null && !idsRecebidos.contains(item.getId())) {
                item.setAtivo(false);
            }
        });
    }

    private void adicionarNovoItem(
            PrescricaoMedicamento prescricao,
            ItemMedicacaoDTO request
    ) {
        ItemMedicacao novoItem = new ItemMedicacao();

        novoItem.setPrescricao(prescricao);
        novoItem.setNomeMedicamento(request.getNomeMedicamento());
        novoItem.setDosagemValor(request.getDosagemValor());
        novoItem.setDosagemUnidade(request.getDosagemUnidade());
        novoItem.setQuantidadeDoses(request.getQuantidadeDoses());
        novoItem.setIntervaloValor(request.getIntervaloValor());
        novoItem.setIntervaloTipo(request.getIntervaloTipo());
        novoItem.setViaAdministracao(request.getViaAdministracao());
        novoItem.setObservacao(request.getObservacao());
        novoItem.setAtivo(true);

        prescricao.getMedicacoes().add(novoItem);
    }

    private void atualizarItem(
            ItemMedicacao item,
            ItemMedicacaoDTO request
    ) {
        item.setNomeMedicamento(request.getNomeMedicamento());
        item.setDosagemValor(request.getDosagemValor());
        item.setDosagemUnidade(request.getDosagemUnidade());
        item.setQuantidadeDoses(request.getQuantidadeDoses());
        item.setIntervaloValor(request.getIntervaloValor());
        item.setIntervaloTipo(request.getIntervaloTipo());
        item.setViaAdministracao(request.getViaAdministracao());
        item.setObservacao(request.getObservacao());
        item.setAtivo(true);
    }
}
