package com.hdc.hdc.prescricao_exercicios.profissional;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicio;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicioMapper;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicioRepository;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicio;
import com.hdc.hdc.prescricao_exercicios.dto.PrescricaoExercicioRequestDTO;
import com.hdc.hdc.prescricao_exercicios.dto.PrescricaoExercicioResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Serviço com as operações de gestão de prescrições de exercícios
 * disponíveis para profissionais de saúde e administradores.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescricaoExercicioProfissionalService {

    private final PrescricaoExercicioRepository prescricaoRepository;
    private final PacienteRepository pacienteRepository;
    private final PrescricaoExercicioMapper mapper;

    // -------------------------------------------------------------------------
    // Operações de escrita
    // -------------------------------------------------------------------------

    @Transactional
    public PrescricaoExercicioResponseDTO criarPrescricao(
            Integer pacienteId,
            PrescricaoExercicioRequestDTO dto,
            Usuario profissional) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        PrescricaoExercicio prescricao = new PrescricaoExercicio();
        prescricao.setPaciente(paciente);
        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());
        prescricao.setAtivo(true);

        List<ItemExercicio> itens = dto.getExercicios().stream()
                .map(itemDto -> mapper.toItemExercicioEntity(itemDto, prescricao))
                .toList();

        prescricao.setExercicios(itens);

        PrescricaoExercicio salva = prescricaoRepository.save(prescricao);
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public PrescricaoExercicioResponseDTO atualizarPrescricao(
            Integer pacienteId,
            UUID prescricaoId,
            PrescricaoExercicioRequestDTO dto,
            Usuario profissional) {

        PrescricaoExercicio prescricao = getPrescricao(prescricaoId);
        validarPertenceAoPaciente(prescricao, pacienteId);

        prescricao.setProfissional(profissional);
        prescricao.setDataInicio(dto.getDataInicio());
        prescricao.setDataFim(dto.getDataFim());
        prescricao.setObservacao(dto.getObservacao());

        prescricao.getExercicios().clear();

        List<ItemExercicio> novosItens = dto.getExercicios().stream()
                .map(itemDto -> mapper.toItemExercicioEntity(itemDto, prescricao))
                .toList();

        prescricao.getExercicios().addAll(novosItens);

        PrescricaoExercicio salva = prescricaoRepository.save(prescricao);
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public void deletarPrescricao(Integer pacienteId, UUID prescricaoId) {
        PrescricaoExercicio prescricao = getPrescricao(prescricaoId);
        validarPertenceAoPaciente(prescricao, pacienteId);
        prescricaoRepository.deleteById(prescricaoId);
    }

    @Transactional
    public PrescricaoExercicioResponseDTO alterarStatus(Integer pacienteId, UUID prescricaoId) {
        PrescricaoExercicio prescricao = getPrescricao(prescricaoId);
        validarPertenceAoPaciente(prescricao, pacienteId);

        prescricao.setAtivo(!prescricao.isAtivo());
        PrescricaoExercicio salva = prescricaoRepository.save(prescricao);
        return mapper.toResponseDTO(salva);
    }

    // -------------------------------------------------------------------------
    // Operações de leitura
    // -------------------------------------------------------------------------

    public List<PrescricaoExercicioResponseDTO> listarPrescricoesAtivas(Integer pacienteId) {
        Date hoje = hoje();
        return prescricaoRepository.findAtivasByPacienteId(pacienteId, hoje).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public List<PrescricaoExercicioResponseDTO> listarHistorico(Integer pacienteId) {
        Date hoje = hoje();
        return prescricaoRepository.findHistoricoByPacienteId(pacienteId, hoje).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private PrescricaoExercicio getPrescricao(UUID id) {
        return prescricaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescrição de exercício não encontrada."));
    }

    private void validarPertenceAoPaciente(PrescricaoExercicio prescricao, Integer pacienteId) {
        if (!prescricao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("A prescrição não pertence a este paciente.");
        }
    }

    private Date hoje() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
