package com.hdc.hdc.service;

import com.hdc.hdc.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.model.PrescricaoMedicamento;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.Usuario;
import com.hdc.hdc.model.RegistroAdesaoMedicamento;
import com.hdc.hdc.model.associacoes.ItemMedicacao;
import com.hdc.hdc.model.enums.StatusAdesao;
import com.hdc.hdc.mapper.PrescricaoMedicamentoMapper;
import com.hdc.hdc.repository.PacienteRepository;
import com.hdc.hdc.repository.PrescricaoMedicamentoRepository;
import com.hdc.hdc.repository.RegistroAdesaoMedicamentoRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescricaoMedicamentoService {

    private final PrescricaoMedicamentoRepository prescricaoRepository;
    private final PacienteRepository pacienteRepository;
    private final RegistroAdesaoMedicamentoRepository adesaoRepository;
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

        PrescricaoMedicamento saved = prescricaoRepository.save(prescricao);
        return mapper.toResponseDTO(saved);
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

        prescricao.setAtivo(false);
        prescricaoRepository.save(prescricao);
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

        List<RegistroAdesaoMedicamento> adesoes = adesaoRepository.findByPrescricaoId(prescricaoId);
        int realizacoes = (int) adesoes.stream().filter(a -> a.getStatus() == StatusAdesao.REALIZADO).count();
        // Considerando o número de registros criados pelo agendador ou paciente. Se não há agendador, 0.
        return getRelatorioAdesaoDTO(prescricaoId, adesoes, realizacoes);
    }

    private static @NonNull RelatorioAdesaoDTO getRelatorioAdesaoDTO(UUID prescricaoId, List<RegistroAdesaoMedicamento> adesoes, int realizacoes) {
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
