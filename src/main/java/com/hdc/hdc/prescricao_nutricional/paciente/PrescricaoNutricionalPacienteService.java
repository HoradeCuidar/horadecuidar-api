package com.hdc.hdc.prescricao_nutricional.paciente;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricional;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.OcorrenciaRefeicao;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.OcorrenciaRefeicaoRepository;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.dto.OcorrenciaRefeicaoResponseDTO;
import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RefeicaoDiaDTO;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalRequestDTO;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalResponseDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.RefeicaoMapper;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicaoMapper;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicaoRepository;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescricaoNutricionalPacienteService {

    private final PacienteRepository pacienteRepository;
    private final OcorrenciaRefeicaoRepository ocorrenciaRefeicaoRepository;
    private final OpcaoRefeicaoRepository opcaoRefeicaoRepository;
    private final RefeicaoMapper refeicaoMapper;
    private final OpcaoRefeicaoMapper opcaoRefeicaoMapper;

    public RefeicaoDiaDTO listarRefeicoesDoDia(Integer pacienteId, LocalDate data) {
        validarPaciente(pacienteId);

        List<OcorrenciaRefeicao> ocorrenciasDia = ocorrenciaRefeicaoRepository
                .findDoDiaByPacienteAndPrescricaoStatus(pacienteId, StatusPrescricao.ATIVA, data);

        List<OcorrenciaRefeicaoResponseDTO> ocorrencias = new ArrayList<>();
        for (OcorrenciaRefeicao ocorrencia : ocorrenciasDia) {
            ocorrencias.add(toOcorrenciaResponseDTO(ocorrencia));
        }

        return new RefeicaoDiaDTO(data, ocorrencias);
    }

    @Transactional
    public RegistroAdesaoNutricionalResponseDTO registrarAdesao(
            Integer pacienteId,
            RegistroAdesaoNutricionalRequestDTO request
    ) {
        validarPaciente(pacienteId);
        validarRequest(request);

        OcorrenciaRefeicao ocorrencia = buscarOcorrencia(request.getOcorrenciaId());
        OpcaoRefeicao opcao = buscarOpcao(request.getOpcaoId());
        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());

        validarPertencimentoAoPaciente(ocorrencia, pacienteId);
        validarPrescricaoDoPaciente(ocorrencia, pacienteId);
        validarOpcaoDaRefeicao(opcao, ocorrencia);
        validarOcorrenciaParaRegistro(ocorrencia, hoje);

        aplicarRegistro(ocorrencia, opcao, request.getObservacao());

        OcorrenciaRefeicao salva = ocorrenciaRefeicaoRepository.save(ocorrencia);
        return toResponseDTO(salva);
    }

    @Transactional
    public RegistroAdesaoNutricionalResponseDTO alterarAdesao(
            Integer pacienteId,
            Integer ocorrenciaId,
            RegistroAdesaoNutricionalRequestDTO request
    ) {
        validarPaciente(pacienteId);

        if (request.getOpcaoId() == null) {
            throw new InvalidValueException("opcaoId", "O ID da opção da refeição é obrigatório.");
        }

        OcorrenciaRefeicao ocorrencia = buscarOcorrencia(ocorrenciaId);
        OpcaoRefeicao opcao = buscarOpcao(request.getOpcaoId());
        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());

        validarPertencimentoAoPaciente(ocorrencia, pacienteId);
        validarPrescricaoDoPaciente(ocorrencia, pacienteId);
        validarOpcaoDaRefeicao(opcao, ocorrencia);
        validarOcorrenciaParaAlteracao(ocorrencia, hoje);

        aplicarRegistro(ocorrencia, opcao, request.getObservacao());

        OcorrenciaRefeicao salva = ocorrenciaRefeicaoRepository.save(ocorrencia);
        return toResponseDTO(salva);
    }

    private void aplicarRegistro(OcorrenciaRefeicao ocorrencia, OpcaoRefeicao opcao, String observacao) {
        ocorrencia.setStatus(StatusAdesao.REALIZADO);
        ocorrencia.setOpcao(opcao);
        ocorrencia.setObservacao(observacao);
        ocorrencia.setDataHoraRegistro(LocalDateTime.now(ZoneId.systemDefault()));
    }

    private void validarPaciente(Integer pacienteId) {
        pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("pacienteId",
                        "Paciente não encontrado."));
    }

    private OcorrenciaRefeicao buscarOcorrencia(Integer ocorrenciaId) {
        return ocorrenciaRefeicaoRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("ocorrenciaId",
                        "Ocorrência de refeição não encontrada."));
    }

    private OpcaoRefeicao buscarOpcao(Integer opcaoId) {
        return opcaoRefeicaoRepository.findById(opcaoId)
                .orElseThrow(() -> new ResourceNotFoundException("opcaoId",
                        "Opção da refeição não encontrada."));
    }

    private void validarRequest(RegistroAdesaoNutricionalRequestDTO request) {
        if (request.getOcorrenciaId() == null) {
            throw new InvalidValueException("ocorrenciaId", "A ocorrência da refeição deve ser informada.");
        }
        if (request.getOpcaoId() == null) {
            throw new InvalidValueException("opcaoId", "A opção da refeição deve ser informada.");
        }
    }

    private void validarPertencimentoAoPaciente(OcorrenciaRefeicao ocorrencia, Integer pacienteId) {
        if (!ocorrencia.getPrescricao().getPacienteId().equals(pacienteId)) {
            throw new InvalidValueException("ocorrenciaId",
                    "A ocorrência não pertence a este paciente.");
        }
    }

    private void validarPrescricaoDoPaciente(OcorrenciaRefeicao ocorrencia, Integer pacienteId) {
        if (!ocorrencia.getPrescricao().getPacienteId().equals(pacienteId)) {
            throw new InvalidValueException("prescricao",
                    "A prescrição não pertence a este paciente.");
        }
    }

    private void validarOpcaoDaRefeicao(OpcaoRefeicao opcao, OcorrenciaRefeicao ocorrencia) {
        if (!opcao.getRefeicao().getId().equals(ocorrencia.getRefeicao().getId())) {
            throw new InvalidValueException("opcaoId",
                    "A opção informada não pertence à refeição desta ocorrência.");
        }
    }

    private void validarOcorrenciaParaRegistro(OcorrenciaRefeicao ocorrencia, LocalDate hoje) {
        validarStatusPermitido(ocorrencia);
        validarPrescricaoAtiva(ocorrencia.getPrescricao());
        validarDataPrevistaDeHoje(ocorrencia, hoje);
    }

    private void validarOcorrenciaParaAlteracao(OcorrenciaRefeicao ocorrencia, LocalDate hoje) {
        validarStatusPermitido(ocorrencia);
        validarPrescricaoAtiva(ocorrencia.getPrescricao());
        validarDataPrevistaDeHoje(ocorrencia, hoje);
    }

    private void validarStatusPermitido(OcorrenciaRefeicao ocorrencia) {
        if (ocorrencia.getStatus() == StatusAdesao.CANCELADO) {
            throw new InvalidValueException("ocorrenciaId",
                    "Não é possível registrar uma ocorrência cancelada.");
        }
    }

    private void validarPrescricaoAtiva(PrescricaoNutricional prescricao) {
        if (prescricao.getStatus() != StatusPrescricao.ATIVA) {
            throw new InvalidValueException("prescricao",
                    "Não é possível registrar uma ocorrência de prescrição inativa.");
        }
    }

    private void validarDataPrevistaDeHoje(OcorrenciaRefeicao ocorrencia, LocalDate hoje) {
        LocalDate dataPrevista = ocorrencia.getDataPrevista();

        if (dataPrevista.isAfter(hoje)) {
            throw new InvalidValueException("ocorrenciaId",
                    "Não é possível registrar uma ocorrência futura.");
        }

        if (dataPrevista.isBefore(hoje)) {
            throw new InvalidValueException("ocorrenciaId",
                    "Não é possível registrar uma ocorrência de dia anterior.");
        }
    }

    private OcorrenciaRefeicaoResponseDTO toOcorrenciaResponseDTO(OcorrenciaRefeicao ocorrencia) {
        return new OcorrenciaRefeicaoResponseDTO(
                ocorrencia.getId(),
                ocorrencia.getPrescricao().getId(),
                ocorrencia.getStatus(),
                refeicaoMapper.modeltoResponseDTO(ocorrencia.getRefeicao()),
                ocorrencia.getOpcao() != null
                        ? opcaoRefeicaoMapper.modeltoResponseDTO(ocorrencia.getOpcao())
                        : null,
                ocorrencia.getDataPrevista(),
                ocorrencia.getOrdemNoDia(),
                ocorrencia.getDataHoraRegistro(),
                ocorrencia.getObservacao()
        );
    }

    private RegistroAdesaoNutricionalResponseDTO toResponseDTO(OcorrenciaRefeicao ocorrencia) {
        return RegistroAdesaoNutricionalResponseDTO.builder()
                .id(ocorrencia.getId())
                .prescricaoId(ocorrencia.getPrescricao().getId())
                .refeicaoId(ocorrencia.getRefeicao().getId())
                .nomeRefeicao(ocorrencia.getRefeicao().getNome())
                .opcaoId(ocorrencia.getOpcao() != null ? ocorrencia.getOpcao().getId() : null)
                .ordemNoDia(ocorrencia.getOrdemNoDia())
                .dataPrevista(ocorrencia.getDataPrevista())
                .status(ocorrencia.getStatus())
                .observacao(ocorrencia.getObservacao())
                .dataHoraRegistro(ocorrencia.getDataHoraRegistro())
                .build();
    }
}
