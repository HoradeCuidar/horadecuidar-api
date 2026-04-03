package com.hdc.hdc.repository;

import com.hdc.hdc.model.PrescricaoMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescricaoMedicamentoRepository extends JpaRepository<PrescricaoMedicamento, UUID> {
    List<PrescricaoMedicamento> findByPacienteIdAndAtivoTrue(Integer pacienteId);

    List<PrescricaoMedicamento> findByPacienteIdAndAtivoFalse(Integer pacienteId);
}
