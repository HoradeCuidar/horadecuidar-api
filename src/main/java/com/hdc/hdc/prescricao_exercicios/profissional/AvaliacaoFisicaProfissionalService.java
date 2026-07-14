package com.hdc.hdc.prescricao_exercicios.profissional;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.prescricao_exercicios.AvaliacaoFisica;
import com.hdc.hdc.prescricao_exercicios.AvaliacaoFisicaMapper;
import com.hdc.hdc.prescricao_exercicios.AvaliacaoFisicaRepository;
import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AvaliacaoFisicaProfissionalService {

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
        
        AvaliacaoFisica salva = avaliacaoFisicaRepository.save(avaliacao);
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public AvaliacaoFisicaResponseDTO atualizarAvaliacao(
            Integer pacienteId,
            UUID avaliacaoId,
            AvaliacaoFisicaRequestDTO dto,
            Usuario profissional) {

        AvaliacaoFisica avaliacao = getAvaliacao(avaliacaoId);
        validarPertenceAoPaciente(avaliacao, pacienteId);

        avaliacao.setProfissional(profissional);
        avaliacao.setDataAtualizacao(LocalDate.now());

        AvaliacaoFisica salva = avaliacaoFisicaRepository.save(avaliacao);
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public void deletarAvaliacao(Integer pacienteId, UUID avaliacaoId) {
        AvaliacaoFisica avaliacao = getAvaliacao(avaliacaoId);
        validarPertenceAoPaciente(avaliacao, pacienteId);
        avaliacaoFisicaRepository.deleteById(avaliacaoId);
    }

    public Page<AvaliacaoFisicaResponseDTO> listarHistorico(Integer pacienteId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageNumber);

        return mapper.toResponseDTO(avaliacaoFisicaRepository.findAll(pageable));
    }

    private AvaliacaoFisica getAvaliacao(UUID id) {
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição de exercício não encontrada."));
    }

    private void validarPertenceAoPaciente(AvaliacaoFisica avaliacao, Integer pacienteId) {
        if (!avaliacao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }
    }
}
