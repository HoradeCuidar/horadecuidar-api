package com.hdc.hdc.prescricao_medicamentos.profissional;

import com.hdc.hdc.adesao_medicamentos.OcorrenciaMedicamento;
import com.hdc.hdc.adesao_medicamentos.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamentoMapper;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamentoRepository;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.adesao_medicamentos.OcorrenciaMedicamentoRepository;
import com.hdc.hdc.util.exception.EntityInUseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescricaoMedicamentoProfissionalService {

    private final PrescricaoMedicamentoRepository prescricaoRepository;
    private final PacienteRepository pacienteRepository;
    private final OcorrenciaMedicamentoRepository ocorrenciaMedicamentoRepository;
    private final PrescricaoMedicamentoMapper mapper;

    @Transactional
    public PrescricaoMedicamentoResponseDTO criarPrescricao(
            Integer pacienteId,
            PrescricaoMedicamentoRequestDTO dto,
            Usuario profissional
    ) {
        Paciente paciente = pacienteRepository
                .findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        PrescricaoMedicamento prescricao = new PrescricaoMedicamento();
        prescricao.setPaciente(paciente);
        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());
        prescricao.setAtivo(true);

        List<ItemMedicacao> itens = dto.getMedicacoes().stream()
                .map(itemDto -> mapper.toItemMedicacaoEntity(itemDto, prescricao))
                .toList();

        prescricao.setMedicacoes(itens);

        PrescricaoMedicamento salva = prescricaoRepository.save(prescricao);
        this.geradorOcorrencias(salva);
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public PrescricaoMedicamentoResponseDTO atualizarPrescricao(
            Integer pacienteId,
            UUID prescricaoId,
            PrescricaoMedicamentoRequestDTO dto,
            Usuario profissional) {
        PrescricaoMedicamento prescricao = this.getPrescricao(prescricaoId);

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());

        prescricao.getMedicacoes().clear();

        List<ItemMedicacao> novosItens = dto.getMedicacoes().stream()
                .map(itemDto -> mapper.toItemMedicacaoEntity(itemDto, prescricao))
                .toList();

        prescricao.getMedicacoes().addAll(novosItens);

        PrescricaoMedicamento saved = prescricaoRepository.save(prescricao);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public void deletarPrescricao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = this.getPrescricao(prescricaoId);

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        try {
            prescricaoRepository.deleteById(prescricaoId);
            prescricaoRepository.flush();
        } catch (DataIntegrityViolationException _) {
            log.info("Orientação medicamentosa não pode ser deletada - integridade referencial");
            throw new EntityInUseException("Orientação Medicamentosa");
        }
    }

    @Transactional
    public PrescricaoMedicamentoResponseDTO alterarStatus(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = this.getPrescricao(prescricaoId);

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        prescricao.setAtivo(!prescricao.isAtivo());
        PrescricaoMedicamento salvo = prescricaoRepository.save(prescricao);
        prescricaoRepository.flush();

        if (salvo.isAtivo()) {
            reativarOcorrencias(salvo);
        } else {
            cancelarOcorrencias(salvo);
        }

        return mapper.toResponseDTO(salvo);
    }

    public List<PrescricaoMedicamentoResponseDTO> listarPrescricoesAtivas(Integer pacienteId) {
        Date hoje = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return prescricaoRepository.findAtivasByPacienteId(pacienteId, hoje).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public List<PrescricaoMedicamentoResponseDTO> listarHistorico(Integer pacienteId) {
        Date hoje = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return prescricaoRepository.findHistoricoByPacienteId(pacienteId, hoje).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public RelatorioAdesaoDTO gerarRelatorioAdesao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = this.getPrescricao(prescricaoId);

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        List<OcorrenciaMedicamento> adesoes = ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricaoId);
        int realizacoes = (int) adesoes.stream().filter(a -> a.getStatus() == StatusAdesao.REALIZADO).count();
        // Considerando o número de registros criados pelo agendador ou paciente. Se não há agendador, 0.
        return getRelatorioAdesaoDTO(prescricaoId, adesoes, realizacoes);
    }

    private static @NonNull RelatorioAdesaoDTO getRelatorioAdesaoDTO(UUID prescricaoId, List<OcorrenciaMedicamento> adesoes, int realizacoes) {
        int totalEsperado = adesoes.size();

        // Total esperado provisório seja no mínimo as realizações (evita divisão por zero)
        if (totalEsperado == 0)
            totalEsperado = realizacoes > 0 ? realizacoes : 1;

        double percentual = ((double) realizacoes / totalEsperado) * 100;

        RelatorioAdesaoDTO dto = new RelatorioAdesaoDTO();
        dto.setPrescricaoId(prescricaoId);
        dto.setDosesRealizadas(realizacoes);
        dto.setTotalDosesEsperadas(totalEsperado);
        dto.setPercentualAdesao(percentual);
        return dto;
    }

    private PrescricaoMedicamento getPrescricao(UUID prescricaoId) {
        return prescricaoRepository.findById(prescricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada."));
    }

    private void geradorOcorrencias(PrescricaoMedicamento prescricao) {
        for (ItemMedicacao item : prescricao.getMedicacoes()) {
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
                    criarOcorrencia(item, data, ordem);
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

    private void cancelarOcorrencias(PrescricaoMedicamento prescricao) {
        List<OcorrenciaMedicamento> ocorrencias = ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId());

        for (OcorrenciaMedicamento ocorrencia : ocorrencias) {
            if(ocorrencia.getStatus() == StatusAdesao.PENDENTE &&
                    (ocorrencia.getDataPrevista().isBefore(prescricao.getDataFim()) && ocorrencia.getDataPrevista().isAfter(LocalDate.now()))) {
                    ocorrencia.setStatus(StatusAdesao.CANCELADO);
                    ocorrenciaMedicamentoRepository.save(ocorrencia);
            }
        }
    }

    private void reativarOcorrencias(PrescricaoMedicamento prescricao) {
        List<OcorrenciaMedicamento> ocorrencias = ocorrenciaMedicamentoRepository.findByPrescricaoId(prescricao.getId());

        for (OcorrenciaMedicamento ocorrencia : ocorrencias) {
            if(ocorrencia.getStatus() == StatusAdesao.CANCELADO &&
                    (ocorrencia.getDataPrevista().isBefore(prescricao.getDataFim()) && ocorrencia.getDataPrevista().isAfter(LocalDate.now()))) {
                ocorrencia.setStatus(StatusAdesao.PENDENTE);
                ocorrenciaMedicamentoRepository.save(ocorrencia);
            }
        }
    }
}
