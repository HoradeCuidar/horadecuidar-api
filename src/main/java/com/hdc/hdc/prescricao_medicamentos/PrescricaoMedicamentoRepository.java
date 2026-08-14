package com.hdc.hdc.prescricao_medicamentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescricaoMedicamentoRepository extends JpaRepository<PrescricaoMedicamento, UUID> {

    @Query("""
        SELECT p FROM PrescricaoMedicamento p
        WHERE p.paciente.id = :pacienteId
                AND p.ativo = true
                AND (p.dataFim IS NULL OR p.dataFim >= :dataRef)
        """)
    List<PrescricaoMedicamento> findAtivasByPacienteId(@Param("pacienteId") Integer pacienteId, @Param("dataRef") LocalDate dataRef);

    @Query("""
        SELECT p FROM PrescricaoMedicamento p
        WHERE p.paciente.id = :pacienteId
                AND (p.ativo = false
                    OR (p.ativo = true AND p.dataFim IS NOT NULL AND p.dataFim < :dataRef)
        )""")
    List<PrescricaoMedicamento> findHistoricoByPacienteId(@Param("pacienteId") Integer pacienteId, @Param("dataRef") LocalDate dataRef);

    List<PrescricaoMedicamento> findByPacienteIdAndAtivoTrue(Integer pacienteId);

    List<PrescricaoMedicamento> findByPacienteIdAndAtivoFalse(Integer pacienteId);
}
