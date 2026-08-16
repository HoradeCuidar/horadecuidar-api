package com.hdc.hdc.prescricao_medicamentos.profissional;

import com.hdc.hdc.adesao.classificacao.CalculadoraClassificacaoAdesao;
import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.OcorrenciaMedicamento;
import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.OcorrenciaMedicamentoValidationService;
import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.dto.RelatorioAdesaoDTO;
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
import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.OcorrenciaMedicamentoRepository;
import com.hdc.hdc.util.exception.EntityInUseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
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
    private final OcorrenciaMedicamentoValidationService ocorrenciaService;
    private final PrescricaoMedicamentoMapper mapper;

    private final CalculadoraClassificacaoAdesao calculadoraClassificacaoAdesao;

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
        prescricaoRepository.flush();

        ocorrenciaService.geradorOcorrencias(prescricao);
        calculadoraClassificacaoAdesao.recalcular(prescricao.getPaciente().getId());

        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public PrescricaoMedicamentoResponseDTO atualizarPrescricao(
            UUID prescricaoId,
            PrescricaoMedicamentoRequestDTO request,
            Usuario profissional
    ) {
        ocorrenciaService.validarPeriodo(request.getDataInicio(), request.getDataFim());

        PrescricaoMedicamento prescricao = prescricaoRepository
                .findById(prescricaoId)
                .orElseThrow(() -> new EntityNotFoundException("Prescrição de medicamento não encontrada."));

        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());

        ocorrenciaService.cancelarOcorrenciasFuturasPendentes(prescricaoId, hoje);

        ocorrenciaService.atualizarDadosDaPrescricao(prescricao, request, profissional);

        ocorrenciaService.sincronizarItens(prescricao, request.getMedicacoes());

        PrescricaoMedicamento prescricaoSalva = prescricaoRepository.save(prescricao);
        prescricaoRepository.flush();

        ocorrenciaService.geradorOcorrencias(prescricaoSalva);
        calculadoraClassificacaoAdesao.recalcular(prescricaoSalva.getPaciente().getId());

        return mapper.toResponseDTO(prescricaoSalva);
    }

    @Transactional
    public void deletarPrescricao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = this.getPrescricao(prescricaoId);

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        boolean possuiAdesao = ocorrenciaMedicamentoRepository.existsByPrescricaoIdAndStatusIn(
                prescricaoId, List.of(StatusAdesao.REALIZADO, StatusAdesao.NAO_REALIZADO)
        );

        if (possuiAdesao) {
            throw new EntityInUseException("Prescrição de Medicamento");
        }

        calculadoraClassificacaoAdesao.recalcular(prescricao.getPaciente().getId());
        ocorrenciaMedicamentoRepository.deleteByPrescricaoId(prescricaoId);
        prescricaoRepository.deleteById(prescricaoId);
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
            ocorrenciaService.reativarOcorrencias(salvo);
        } else {
            ocorrenciaService.cancelarOcorrencias(salvo);
        }

        calculadoraClassificacaoAdesao.recalcular(prescricao.getPaciente().getId());

        return mapper.toResponseDTO(salvo);
    }

    public List<PrescricaoMedicamentoResponseDTO> listarPrescricoesAtivas(Integer pacienteId) {
        return prescricaoRepository.findAtivasByPacienteId(pacienteId, LocalDate.now(ZoneId.systemDefault())).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public List<PrescricaoMedicamentoResponseDTO> listarHistorico(Integer pacienteId) {
        return prescricaoRepository.findHistoricoByPacienteId(pacienteId, LocalDate.now(ZoneId.systemDefault())).stream()
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
}
