package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescricaoNutricionalRepository extends JpaRepository<PrescricaoNutricional, Integer> {

    List<PrescricaoNutricional> findAllByPacienteId(Integer integer);

    boolean existsByPacienteIdAndStatus(Integer pacienteId, StatusPrescricao status);
}