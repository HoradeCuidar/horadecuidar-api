package com.hdc.hdc.avaliacao_fisica;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AvaliacaoFisicaService {

    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final PacienteRepository pacienteRepository;
    private final AvaliacaoFisicaMapper mapper;

    @Transactional
    public AvaliacaoFisicaResponseDTO registrarAvaliacao(
            Integer pacienteId,
            AvaliacaoFisicaRequestDTO dto,
            Usuario profissional) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        AvaliacaoFisica avaliacao = mapper.toEntity(dto, paciente.getId(), profissional);
        avaliacao.setDataRegistro(LocalDate.now());
        avaliacao.setPaciente(paciente);
        avaliacao.setProfissional(profissional);

        AvaliacaoFisica salva = avaliacaoFisicaRepository.save(avaliacao);
        return mapper.toResponseDTO(salva);
    }

    public Object listarAvaliacoes(Integer pacienteId, Usuario usuario) {
        if (usuario.getRole() == Role.PACIENTE) {
            Optional<AvaliacaoFisica> ultima = avaliacaoFisicaRepository.findFirstByPacienteIdOrderByDataRegistroDesc(pacienteId);
            return ultima.map(mapper::toResponseDTO).map(List::of).orElse(Collections.emptyList());
        } else {
            Pageable pageable = PageRequest.of(0, 1000); // Assuming we want all or a reasonable max
            Page<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByPacienteIdOrderByDataRegistroDesc(pacienteId, pageable);
            return avaliacoes.map(mapper::toResponseDTO).getContent();
        }
    }

    public AvaliacaoFisicaResponseDTO buscarPorId(Integer pacienteId, UUID avaliacaoId) {
        AvaliacaoFisica avaliacao = getAvaliacao(avaliacaoId);
        validarPertenceAoPaciente(avaliacao, pacienteId);
        return mapper.toResponseDTO(avaliacao);
    }

    @Transactional
    public AvaliacaoFisicaResponseDTO atualizarAvaliacao(
            Integer pacienteId,
            UUID avaliacaoId,
            AvaliacaoFisicaRequestDTO dto,
            Usuario profissional) {

        AvaliacaoFisica avaliacao = getAvaliacao(avaliacaoId);
        validarPertenceAoPaciente(avaliacao, pacienteId);

        if (!avaliacao.getProfissional().getId().equals(profissional.getId())) {
            throw new IllegalArgumentException("Apenas o profissional que criou a avaliação pode editá-la.");
        }

        // Atualizar campos
        avaliacao.setRealizaAtividadeFisica(dto.realizaAtividadeFisica());
        avaliacao.setAtividadeRealizada(dto.atividadeRealizada());
        avaliacao.setFrequenciaSemanal(dto.frequenciaSemanal());
        avaliacao.setFlexibilidade(dto.flexibilidade());
        avaliacao.setForcaPalmarDireita(dto.forcaPalmarDireita());
        avaliacao.setForcaPalmarEsquerda(dto.forcaPalmarEsquerda());
        avaliacao.setAssimetriaPalmar(dto.assimetriaPalmar());
        avaliacao.setForcaJoelhoDireita(dto.forcaJoelhoDireita());
        avaliacao.setForcaJoelhoEsquerda(dto.forcaJoelhoEsquerda());
        avaliacao.setAssimetriaJoelho(dto.assimetriaJoelho());
        avaliacao.setQueixas(dto.queixas());
        avaliacao.setObservacoesMusculoEsqueleticas(dto.observacoesMusculoEsqueleticas());
        avaliacao.setOrientacoesGerais(dto.orientacoesGerais());
        
        avaliacao.setDataAtualizacao(LocalDate.now());

        AvaliacaoFisica salva = avaliacaoFisicaRepository.save(avaliacao);
        return mapper.toResponseDTO(salva);
    }

    private AvaliacaoFisica getAvaliacao(UUID id) {
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação física não encontrada."));
    }

    private void validarPertenceAoPaciente(AvaliacaoFisica avaliacao, Integer pacienteId) {
        if (!avaliacao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A avaliação não pertence a este paciente.");
        }
    }
}
