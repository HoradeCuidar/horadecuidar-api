package com.hdc.hdc.prescricao_nutricional.profissional;

import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricional;
import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricionalMapper;
import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricionalRepository;
import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescrito;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResponseDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResumoDTO;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescricaoNutricionalProfissionalService {

    private final PrescricaoNutricionalRepository prescricaoNutricionalRepository;
    private final PrescricaoNutricionalMapper prescricaoNutricionalMapper;

    @Autowired
    PrescricaoNutricionalProfissionalService(PrescricaoNutricionalRepository prescricaoNutricionalRepository,
                                             PrescricaoNutricionalMapper prescricaoNutricionalMapper){
        this.prescricaoNutricionalRepository = prescricaoNutricionalRepository;
        this.prescricaoNutricionalMapper = prescricaoNutricionalMapper;
    }

    @Transactional
    public PrescricaoNutricionalResponseDTO cadastrar(PrescricaoNutricional prescricaoNutricional) {

        if (prescricaoNutricional.getDataInicio().isAfter(prescricaoNutricional.getDataFim())) {
            throw new InvalidValueException(
                    "A data de início não pode ser posterior à data final."
            );
        }

        vincularEntidades(prescricaoNutricional);

        prescricaoNutricional.setStatus(StatusPrescricao.ATIVA);

        return prescricaoNutricionalMapper.modeltoResponseDTO(prescricaoNutricionalRepository.save(prescricaoNutricional));
    }

    @Transactional()
    public PrescricaoNutricionalResponseDTO visualizar(Integer id_prescricao){
        return prescricaoNutricionalMapper.modeltoResponseDTO(
                prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"))
                );
    }

    public List<PrescricaoNutricionalResumoDTO> visualizarTodos(Integer id_paciente){
        return prescricaoNutricionalMapper.modeltoResumoDTO(
                prescricaoNutricionalRepository.findAllByPacienteId(id_paciente));
    }

    public PrescricaoNutricionalResumoDTO ativarPrescricao(Integer id_prescricao){
        PrescricaoNutricional prescricaoNutricional = prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"));

        prescricaoNutricional.setStatus(StatusPrescricao.ATIVA);

        return prescricaoNutricionalMapper.modeltoResumoDTO(
                prescricaoNutricionalRepository.save(prescricaoNutricional));
    }

    public PrescricaoNutricionalResumoDTO inativarPrescricao(Integer id_prescricao){
        PrescricaoNutricional prescricaoNutricional = prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"));

        prescricaoNutricional.setStatus(StatusPrescricao.INATIVA);

        return prescricaoNutricionalMapper.modeltoResumoDTO(
                prescricaoNutricionalRepository.save(prescricaoNutricional));
    }

    @Transactional
    public PrescricaoNutricionalResponseDTO editar(Integer id_prescricao,
                                                   PrescricaoNutricional novaPrescricaoNutricional) {

        PrescricaoNutricional antigaPrescricaoNutricional = prescricaoNutricionalRepository.findById(id_prescricao)
                        .orElseThrow(() -> new ResourceNotFoundException("Prescrição nutricional não encontrada"));

        if (antigaPrescricaoNutricional.getStatus() == StatusPrescricao.ENCERRADA) {
            throw new InvalidValueException(
                    "Não é possível editar uma prescrição encerrada!"
            );
        }

        novaPrescricaoNutricional.setId(id_prescricao);
        novaPrescricaoNutricional.setPacienteId(antigaPrescricaoNutricional.getPacienteId());
        novaPrescricaoNutricional.setProfissionalId(antigaPrescricaoNutricional.getProfissionalId());
        novaPrescricaoNutricional.setDataInicio(antigaPrescricaoNutricional.getDataInicio());
        novaPrescricaoNutricional.setStatus(antigaPrescricaoNutricional.getStatus());

        vincularEntidades(novaPrescricaoNutricional);

        return prescricaoNutricionalMapper.modeltoResponseDTO(prescricaoNutricionalRepository.save(novaPrescricaoNutricional));
    }

    // MÉTODOS AUXILIARES
    private void vincularEntidades(PrescricaoNutricional prescricaoNutricional) {

        for (Refeicao refeicao : prescricaoNutricional.getRefeicoes()) {
            refeicao.setPrescricao(prescricaoNutricional);
            for (OpcaoRefeicao opcao : refeicao.getOpcoes()) {
                opcao.setRefeicao(refeicao);
                for (AlimentoPrescrito alimento : opcao.getAlimentos()) {
                    alimento.setOpcao(opcao);
                }
            }
        }
    }

}