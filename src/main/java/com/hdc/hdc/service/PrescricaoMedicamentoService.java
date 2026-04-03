package com.hdc.hdc.service;

import com.hdc.hdc.dto.ItemMedicacaoDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.PrescricaoMedicamento;
import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.model.RegistroAdesaoMedicamento;
import com.hdc.hdc.model.associacoes.ItemMedicacao;
import com.hdc.hdc.model.enums.StatusAdesao;
import com.hdc.hdc.repository.PacienteRepository;
import com.hdc.hdc.repository.PrescricaoMedicamentoRepository;
import com.hdc.hdc.repository.ProfissionalDaSaudeRepository;
import com.hdc.hdc.repository.RegistroAdesaoMedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescricaoMedicamentoService {

    @Autowired
    private PrescricaoMedicamentoRepository prescricaoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalDaSaudeRepository profissionalRepository;

    @Autowired
    private RegistroAdesaoMedicamentoRepository adesaoRepository;

    @Transactional
    public PrescricaoMedicamentoResponseDTO criarPrescricao(Integer pacienteId, PrescricaoMedicamentoRequestDTO dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        ProfissionalDaSaude profissional = profissionalRepository.findById(dto.getProfissionalId())
                .orElseThrow(() -> new IllegalArgumentException("Profissional de saúde não encontrado."));

        PrescricaoMedicamento prescricao = new PrescricaoMedicamento();
        prescricao.setPaciente(paciente);
        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());
        prescricao.setAtivo(true);

        List<ItemMedicacao> itens = dto.getMedicacoes().stream().map(itemDto -> {
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
        }).collect(Collectors.toList());

        prescricao.setMedicacoes(itens);

        PrescricaoMedicamento saved = prescricaoRepository.save(prescricao);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public PrescricaoMedicamentoResponseDTO atualizarPrescricao(Integer pacienteId, UUID prescricaoId,
            PrescricaoMedicamentoRequestDTO dto) {
        PrescricaoMedicamento prescricao = prescricaoRepository.findById(prescricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada."));

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        // Se já existe adesão, optamos por criar uma nova versão (ou atualizar direto
        // se for simples)
        // Por simplicidade na implementação MVP, vamos apenas atualizar os dados

        ProfissionalDaSaude profissional = profissionalRepository.findById(dto.getProfissionalId())
                .orElseThrow(() -> new IllegalArgumentException("Profissional de saúde não encontrado."));

        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());

        prescricao.getMedicacoes().clear();

        List<ItemMedicacao> novosItens = dto.getMedicacoes().stream().map(itemDto -> {
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
        }).collect(Collectors.toList());

        prescricao.getMedicacoes().addAll(novosItens);

        PrescricaoMedicamento saved = prescricaoRepository.save(prescricao);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public void deletarPrescricao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = prescricaoRepository.findById(prescricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada."));

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        // Deleção lógica
        prescricao.setAtivo(false);
        prescricaoRepository.save(prescricao);
    }

    public List<PrescricaoMedicamentoResponseDTO> listarPrescricoesAtivas(Integer pacienteId) {
        LocalDate hoje = LocalDate.now();
        return prescricaoRepository.findByPacienteIdAndAtivoTrue(pacienteId).stream()
                .filter(p -> p.getDataFim() == null
                        || !p.getDataFim().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(hoje))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PrescricaoMedicamentoResponseDTO> listarHistorico(Integer pacienteId) {
        LocalDate hoje = LocalDate.now();
        List<PrescricaoMedicamento> inativas = prescricaoRepository.findByPacienteIdAndAtivoFalse(pacienteId);
        List<PrescricaoMedicamento> ativasVencidas = prescricaoRepository.findByPacienteIdAndAtivoTrue(pacienteId)
                .stream()
                .filter(p -> p.getDataFim() != null
                        && p.getDataFim().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(hoje))
                .collect(Collectors.toList());

        List<PrescricaoMedicamento> historico = new ArrayList<>(inativas);
        historico.addAll(ativasVencidas);

        return historico.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public RelatorioAdesaoDTO gerarRelatorioAdesao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoMedicamento prescricao = prescricaoRepository.findById(prescricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição não encontrada."));

        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }

        List<RegistroAdesaoMedicamento> adesoes = adesaoRepository.findByPrescricaoId(prescricaoId);
        int realizacoes = (int) adesoes.stream().filter(a -> a.getStatus() == StatusAdesao.REALIZADO).count();
        int totalEsperado = adesoes.size(); // Para o MVP, considera-se o número de registros criados pelo agendador ou
                                            // paciente. Se não há agendador, 0.

        // Em um cenário real, totalEsperado seria calculado baseado na dataInicio,
        // dataFim, intervaloTipo e intervaloValor do ItemMedicacao.
        // Como o documento de requisitos menciona isso como ponto aberto, e a modelagem
        // simples foi aceita, faremos um cálculo simplificado.
        // Simulando que o total esperado provisório seja no mínimo as realizações
        // (evita divide by zero).
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

    private PrescricaoMedicamentoResponseDTO mapToResponseDTO(PrescricaoMedicamento prescricao) {
        PrescricaoMedicamentoResponseDTO dto = new PrescricaoMedicamentoResponseDTO();
        dto.setId(prescricao.getId());
        dto.setPacienteId(prescricao.getPaciente().getId());
        dto.setProfissionalId(prescricao.getProfissional().getId());
        dto.setNomeProfissional(prescricao.getProfissional().getNome());
        dto.setDataInicio(prescricao.getDataInicio());
        dto.setDataFim(prescricao.getDataFim());
        dto.setObservacao(prescricao.getObservacao());
        dto.setAtivo(prescricao.isAtivo());

        List<ItemMedicacaoDTO> itensDto = prescricao.getMedicacoes().stream().map(item -> {
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
        }).collect(Collectors.toList());

        dto.setMedicacoes(itensDto);
        return dto;
    }
}
