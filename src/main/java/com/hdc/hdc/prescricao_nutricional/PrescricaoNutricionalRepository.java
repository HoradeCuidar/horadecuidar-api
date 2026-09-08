package com.hdc.hdc.prescricao_nutricional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescricaoNutricionalRepository extends JpaRepository<PrescricaoNutricional, Integer> {

    List<PrescricaoNutricional> findAllByPacienteId(Integer integer);
}