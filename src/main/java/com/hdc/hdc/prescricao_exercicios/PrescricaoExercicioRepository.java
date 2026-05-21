package com.hdc.hdc.prescricao_exercicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescricaoExercicioRepository extends JpaRepository<PrescricaoExercicio, UUID> {

    /**
     * Retorna prescrições ativas cuja data de fim ainda não passou ou é indefinida.
     */
    @Query("""
        SELECT p FROM PrescricaoExercicio p
        WHERE p.paciente.id = :pacienteId
              AND p.ativo = true
              AND (p.dataFim IS NULL OR p.dataFim >= :dataRef)
        """)
    List<PrescricaoExercicio> findAtivasByPacienteId(
            @Param("pacienteId") Integer pacienteId,
            @Param("dataRef") Date dataRef);

    /**
     * Retorna o histórico: prescrições inativas ou encerradas.
     */
    @Query("""
        SELECT p FROM PrescricaoExercicio p
        WHERE p.paciente.id = :pacienteId
              AND (p.ativo = false
                   OR (p.ativo = true AND p.dataFim IS NOT NULL AND p.dataFim < :dataRef))
        """)
    List<PrescricaoExercicio> findHistoricoByPacienteId(
            @Param("pacienteId") Integer pacienteId,
            @Param("dataRef") Date dataRef);

    List<PrescricaoExercicio> findByPacienteIdAndAtivoTrue(Integer pacienteId);
}
