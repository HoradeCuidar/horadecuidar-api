package com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos;

import com.hdc.hdc.medicamentos.Medicamento;
import com.hdc.hdc.medicamentos.MedicamentoService;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.enums.IntervaloTipo;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import com.hdc.hdc.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcorrenciaMedicamentoValidationService {

    private final OcorrenciaMedicamentoRepository ocorrenciaMedicamentoRepository;
    private final MedicamentoService medicamentoService;

    /** Gera a agenda inicial de forma idempotente. */
    public void geradorOcorrencias(PrescricaoMedicamento prescricao) {
        sincronizarOcorrencias(prescricao, LocalDate.now(ZoneId.systemDefault()));
    }

    /**
     * Sincroniza somente datas posteriores a hoje. Registros do paciente e o
     * histórico do dia atual ou passado nunca são alterados.
     */
    public void sincronizarOcorrencias(PrescricaoMedicamento prescricao, LocalDate hoje) {
        List<OcorrenciaMedicamento> existentes =
                ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId());

        Map<ChaveOcorrencia, OcorrenciaMedicamento> ocorrenciasPorChave =
                existentes.stream().collect(Collectors.toMap(this::toChave, Function.identity()));
        Set<ChaveOcorrencia> desejadas = calcularOcorrenciasDesejadas(prescricao);

        for (OcorrenciaMedicamento ocorrencia : existentes) {
            boolean podeAlterar = hoje != null && ocorrencia.getDataPrevista().isAfter(hoje);
            if (!podeAlterar) {
                continue;
            }

            boolean aindaDesejada = prescricao.isAtivo() && desejadas.contains(toChave(ocorrencia));
            if (aindaDesejada && ocorrencia.getStatus() == StatusAdesao.CANCELADO) {
                ocorrencia.reativar();
            } else if (!aindaDesejada && ocorrencia.getStatus() == StatusAdesao.PENDENTE) {
                ocorrencia.cancelar();
            }
        }

        if (!prescricao.isAtivo()) {
            return;
        }

        Map<Long, ItemMedicacao> itensPorId = prescricao.getMedicacoes().stream()
                .filter(ItemMedicacao::isAtivo)
                .collect(Collectors.toMap(ItemMedicacao::getId, Function.identity()));

        for (ChaveOcorrencia chave : desejadas) {
            boolean podeCriar = hoje == null || chave.dataPrevista().isAfter(hoje);
            if (podeCriar && !ocorrenciasPorChave.containsKey(chave)) {
                criarOcorrencia(
                        itensPorId.get(chave.itemMedicacaoId()),
                        chave.dataPrevista(),
                        chave.ordemNoDia()
                );
            }
        }
    }

    public void cancelarOcorrenciasFuturas(PrescricaoMedicamento prescricao, LocalDate hoje) {
        ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId()).stream()
                .filter(OcorrenciaMedicamento::estaPendente)
                .filter(ocorrencia -> ocorrencia.getDataPrevista().isAfter(hoje))
                .forEach(OcorrenciaMedicamento::cancelar);
    }

    private Set<ChaveOcorrencia> calcularOcorrenciasDesejadas(PrescricaoMedicamento prescricao) {
        Set<ChaveOcorrencia> desejadas = new HashSet<>();

        for (ItemMedicacao item : prescricao.getMedicacoes()) {
            if (!item.isAtivo()) {
                continue;
            }
            validarConfiguracaoDoItem(item);

            for (LocalDate data = prescricao.getDataInicio();
                 !data.isAfter(prescricao.getDataFim());
                 data = data.plusDays(1)) {
                if (!deveGerarNoDia(item, prescricao.getDataInicio(), data)) {
                    continue;
                }
                for (int ordem = 1; ordem <= item.getQuantidadeDoses(); ordem++) {
                    desejadas.add(new ChaveOcorrencia(item.getId(), data, ordem));
                }
            }
        }

        return desejadas;
    }

    private boolean deveGerarNoDia(ItemMedicacao item, LocalDate inicio, LocalDate data) {
        int intervalo = item.getIntervaloValor();
        IntervaloTipo tipo = item.getIntervaloTipo();

        return switch (tipo) {
            case HORA -> true;
            case DIA -> ChronoUnit.DAYS.between(inicio, data) % intervalo == 0;
            case SEMANA -> ChronoUnit.WEEKS.between(inicio, data) % intervalo == 0
                    && inicio.getDayOfWeek() == data.getDayOfWeek();
            case MES -> ChronoUnit.MONTHS.between(inicio, data) % intervalo == 0
                    && inicio.getDayOfMonth() == data.getDayOfMonth();
        };
    }

    private void validarConfiguracaoDoItem(ItemMedicacao item) {
        if (item.getId() == null) {
            throw new IllegalStateException("O item de medicação deve ser salvo antes de gerar ocorrências.");
        }
        if (item.getQuantidadeDoses() == null || item.getQuantidadeDoses() <= 0) {
            throw new IllegalArgumentException("A quantidade de doses deve ser maior que zero.");
        }
        if (item.getIntervaloValor() == null || item.getIntervaloValor() <= 0 || item.getIntervaloTipo() == null) {
            throw new IllegalArgumentException("O intervalo da medicação deve ser válido.");
        }
    }

    private ChaveOcorrencia toChave(OcorrenciaMedicamento ocorrencia) {
        return new ChaveOcorrencia(
                ocorrencia.getItemMedicacao().getId(),
                ocorrencia.getDataPrevista(),
                ocorrencia.getOrdemNoDia()
        );
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

    public void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas inicial e final são obrigatórias.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final.");
        }
    }

    public void validarAtualizacaoDoPeriodo(
            PrescricaoMedicamento prescricao,
            PrescricaoMedicamentoRequestDTO request
    ) {
        if (request.getDataInicio().isBefore(prescricao.getDataInicio())) {
            throw new IllegalArgumentException("Não é possível estender a prescrição para o passado.");
        }
    }

    public void sincronizarItens(PrescricaoMedicamento prescricao, List<ItemMedicacaoDTO> itensRequest) {
        if (itensRequest == null || itensRequest.isEmpty()) {
            throw new IllegalArgumentException("A prescrição deve possuir pelo menos um medicamento.");
        }

        validarIdsDuplicados(itensRequest);

        Map<Long, ItemMedicacao> itensExistentes = prescricao.getMedicacoes().stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(ItemMedicacao::getId, Function.identity()));

        Set<Long> idsRecebidos = itensRequest.stream()
                .map(ItemMedicacaoDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (idsRecebidos.stream().anyMatch(id -> !itensExistentes.containsKey(id))) {
            throw new IllegalArgumentException("Um dos medicamentos informados não pertence à prescrição.");
        }

        prescricao.getMedicacoes().forEach(item -> {
            if (item.getId() != null && !idsRecebidos.contains(item.getId())) {
                item.setAtivo(false);
            }
        });

        for (ItemMedicacaoDTO itemRequest : itensRequest) {
            validarConfiguracaoDoItemRequest(itemRequest);
            if (itemRequest.getId() == null) {
                ItemMedicacao novoItem = new ItemMedicacao();
                novoItem.setPrescricao(prescricao);
                atualizarItem(novoItem, itemRequest);
                prescricao.getMedicacoes().add(novoItem);
            } else {
                atualizarItem(itensExistentes.get(itemRequest.getId()), itemRequest);
            }
        }
    }

    private void validarIdsDuplicados(List<ItemMedicacaoDTO> itensRequest) {
        Set<Long> ids = new HashSet<>();
        boolean possuiIdDuplicado = itensRequest.stream()
                .map(ItemMedicacaoDTO::getId)
                .filter(Objects::nonNull)
                .anyMatch(id -> !ids.add(id));

        if (possuiIdDuplicado) {
            throw new IllegalArgumentException("Um item de medicação não pode ser informado mais de uma vez.");
        }
    }

    private void validarConfiguracaoDoItemRequest(ItemMedicacaoDTO item) {
        if (item.getQuantidadeDoses() == null || item.getQuantidadeDoses() <= 0) {
            throw new IllegalArgumentException("A quantidade de doses deve ser maior que zero.");
        }
        if (item.getIntervaloValor() == null || item.getIntervaloValor() <= 0 || item.getIntervaloTipo() == null) {
            throw new IllegalArgumentException("O intervalo da medicação deve ser válido.");
        }
    }

    private void atualizarItem(ItemMedicacao item, ItemMedicacaoDTO request) {
        Medicamento medicamento = medicamentoService.buscarOuCriar(request.getNomeMedicamento());
        item.setMedicamento(medicamento);
        item.setNomeMedicamento(medicamento.getNome());
        item.setDosagemValor(request.getDosagemValor());
        item.setDosagemUnidade(request.getDosagemUnidade());
        item.setQuantidadeDoses(request.getQuantidadeDoses());
        item.setIntervaloValor(request.getIntervaloValor());
        item.setIntervaloTipo(request.getIntervaloTipo());
        item.setViaAdministracao(request.getViaAdministracao());
        item.setObservacao(request.getObservacao());
        item.setAtivo(true);
    }

    public record ChaveOcorrencia(Long itemMedicacaoId, LocalDate dataPrevista, Integer ordemNoDia) {
    }
}
