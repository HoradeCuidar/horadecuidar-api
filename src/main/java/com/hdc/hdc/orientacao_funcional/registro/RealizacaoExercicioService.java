package com.hdc.hdc.orientacao_funcional.registro;

import com.hdc.hdc.orientacao_funcional.orientacao.OrientacaoFuncional;
import com.hdc.hdc.orientacao_funcional.orientacao.OrientacaoFuncionalRepository;
import com.hdc.hdc.orientacao_funcional.registro.dto.RegistroRealizacaoFuncionalRequestDTO;
import com.hdc.hdc.orientacao_funcional.registro.dto.RegistroRealizacaoFuncionalResponseDTO;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RealizacaoExercicioService {

    private final OrientacaoFuncionalRepository orientacaoRepository;
    private final RealizacaoExercicioRepository realizacaoRepository;
    private final PacienteRepository pacienteRepository;

    /**
     * Registra a realização de uma orientação funcional pelo paciente.
     */
    @Transactional
    public RegistroRealizacaoFuncionalResponseDTO registrarRealizacao(Integer pacienteId, RegistroRealizacaoFuncionalRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado."));

        OrientacaoFuncional orientacao = orientacaoRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Orientação funcional não encontrada."));

        LocalDateTime agora = LocalDateTime.now();
//        LocalDateTime inicioDia = agora.toLocalDate().atStartOfDay();
//        LocalDateTime fimDia = inicioDia.plusDays(1);

        // Não permite duplicatas no mesmo dia para a mesma orientação.
//        boolean jaRegistrouHoje = realizacaoRepository
//                .existsByOrientacaoFuncionalIdAndPacienteIdAndDataRegistroBetween(
//                        orientacaoId, pacienteId, inicioDia, fimDia);
//
//        if (jaRegistrouHoje) {
//            throw new IllegalArgumentException("Já existe um registro para esta orientação hoje. Use a operação de alteração.");
//        }

        RealizacaoExercicio realizacao = new RealizacaoExercicio();
        realizacao.setOrientacaoFuncional(orientacao);
        realizacao.setPaciente(paciente);
        realizacao.setStatus(request.status());
        realizacao.setDuracaoRealizadaMinutos(request.duracaoRealizadaMinutos());
        realizacao.setSensacaoFinal(request.sensacaoFinal());
        realizacao.setObservacao(request.observacao());
        realizacao.setDataRegistro(agora);

        RealizacaoExercicio salva = realizacaoRepository.save(realizacao);
        return toDTO(salva);
    }

    /*
     * Altera um registro existente — apenas no mesmo dia em que foi criado.
     */
    @Transactional
    public RegistroRealizacaoFuncionalResponseDTO alterarRealizacao(Integer pacienteId, Long registroId, RegistroRealizacaoFuncionalRequestDTO request) {
        RealizacaoExercicio realizacao = this.buscarRegistro(registroId);

        this.validarPacienteEPermissaoAlteracao(pacienteId, realizacao);

        realizacao.setStatus(request.status());
        realizacao.setDuracaoRealizadaMinutos(request.duracaoRealizadaMinutos());
        realizacao.setSensacaoFinal(request.sensacaoFinal());
        realizacao.setObservacao(request.observacao());

        RealizacaoExercicio atualizada = realizacaoRepository.save(realizacao);
        return toDTO(atualizada);
    }

    /*
     * Retorna o histórico de realizações de um paciente.
     */
    public Page<RegistroRealizacaoFuncionalResponseDTO> historicoDoPaciente(Integer pacienteId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataRegistro"));
        return realizacaoRepository.findAllByPacienteId(pageable, pacienteId)
                .map(this::toDTO);
    }

    /*
     * Altera o status de realização funcional já registrada pelo participante
     */
    @Transactional
    public RegistroRealizacaoFuncionalResponseDTO alterarStatus(Integer pacienteId, Long registroId, StatusRealizacao status) {
        RealizacaoExercicio realizacao = this.buscarRegistro(registroId);

        this.validarPacienteEPermissaoAlteracao(pacienteId, realizacao);

        realizacao.setStatus(status);

        RealizacaoExercicio atualizada = realizacaoRepository.save(realizacao);
        return toDTO(atualizada);
    }

    /*
     * Altera o status de realização funcional já registrada pelo participante
     */
    @Transactional
    public RegistroRealizacaoFuncionalResponseDTO alterarSensacaoFinal(Integer pacienteId, Long registroId, SensacaoFinal sensasao) {
        RealizacaoExercicio realizacao = this.buscarRegistro(registroId);

        this.validarPacienteEPermissaoAlteracao(pacienteId, realizacao);

        realizacao.setSensacaoFinal(sensasao);

        RealizacaoExercicio atualizada = realizacaoRepository.save(realizacao);
        return toDTO(atualizada);
    }

    // -- helpers --

    private RegistroRealizacaoFuncionalResponseDTO toDTO(RealizacaoExercicio r) {
        return new RegistroRealizacaoFuncionalResponseDTO(
                r.getId(),
                r.getOrientacaoFuncional().getId(),
                r.getOrientacaoFuncional().getNome(),
                r.getStatus(),
                r.getDuracaoRealizadaMinutos(),
                r.getSensacaoFinal(),
                r.getObservacao(),
                r.getDataRegistro()
        );
    }
    
    private RealizacaoExercicio buscarRegistro(Long registroId) {
        return realizacaoRepository.findById(registroId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de realização não encontrado."));
    }

    private void validarPacienteEPermissaoAlteracao(Integer pacienteId, RealizacaoExercicio realizacao) {
        if (!realizacao.getPaciente().getId().equals(pacienteId)) {
            throw new IllegalArgumentException("O registro não pertence a este paciente.");
        }

        if (!realizacao.getDataRegistro().toLocalDate().equals(java.time.LocalDate.now())) {
            throw new IllegalStateException("Registros só podem ser alterados no mesmo dia em que foram criados.");
        }
    }
}
