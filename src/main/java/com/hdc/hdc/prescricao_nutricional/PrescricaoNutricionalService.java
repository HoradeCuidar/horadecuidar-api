package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescrito;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResponseDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResumoDTO;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescricaoNutricionalService {

    private final PrescricaoNutricionalRepository prescricaoNutricionalRepository;
    private final PrescricaoNutricionalMapper prescricaoNutricionalMapper;

    @Autowired
    PrescricaoNutricionalService(PrescricaoNutricionalRepository prescricaoNutricionalRepository,
                                 PrescricaoNutricionalMapper prescricaoNutricionalMapper){
        this.prescricaoNutricionalRepository = prescricaoNutricionalRepository;
        this.prescricaoNutricionalMapper = prescricaoNutricionalMapper;
    }

    @Transactional
    public PrescricaoNutricionalResponseDTO cadastrar(PrescricaoNutricional prescricaoNutricional) {

        validarDatas(prescricaoNutricional);

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

    private void validarDatas(PrescricaoNutricional prescricaoNutricional) {

        if (prescricaoNutricional.getDataInicio().isAfter(prescricaoNutricional.getDataFim())) {
            throw new IllegalArgumentException(
                    "A data de início não pode ser posterior à data final."
            );
        }
    }

    private void vincularEntidades(PrescricaoNutricional prescricao) {

        for (Refeicao refeicao : prescricao.getRefeicoes()) {
            refeicao.setPrescricao(prescricao);
            for (OpcaoRefeicao opcao : refeicao.getOpcoes()) {
                opcao.setRefeicao(refeicao);
                for (AlimentoPrescrito alimento : opcao.getAlimentos()) {
                    alimento.setOpcao(opcao);
                }
            }
        }
    }
}