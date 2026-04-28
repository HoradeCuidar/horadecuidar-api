package com.hdc.hdc.adesao_medicamentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdesaoMedicamentoRepository extends JpaRepository<AdesaoMedicamento, Long> {

    List<AdesaoMedicamento> findByPrescricaoId(UUID prescricaoId);

    boolean existsByPrescricaoId(UUID prescricaoId);

    @Query("""
        SELECT a FROM AdesaoMedicamento a
        WHERE a.itemMedicacao.id = :itemMedicacaoId
              AND a.dataHoraRegistro >= :inicioDia
              AND a.dataHoraRegistro < :fimDia
        """)
    List<AdesaoMedicamento> findByItemMedicacaoIdAndDia(
            @Param("itemMedicacaoId") Long itemMedicacaoId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia);

    @Query("""
        SELECT a FROM AdesaoMedicamento a
        WHERE a.itemMedicacao.id = :itemMedicacaoId
              AND a.dataHoraRegistro >= :inicioDia
              AND a.dataHoraRegistro < :fimDia
        ORDER BY a.dataHoraRegistro DESC
        """)
    Optional<AdesaoMedicamento> findUltimaByItemMedicacaoIdAndDia(
            @Param("itemMedicacaoId") Long itemMedicacaoId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia);

    @Query("""
        SELECT a FROM AdesaoMedicamento a
        WHERE a.prescricao.paciente.id = :pacienteId
              AND a.dataHoraRegistro >= :inicio
              AND a.dataHoraRegistro < :fim
        """)
    List<AdesaoMedicamento> findByPacienteIdAndPeriodo(
            @Param("pacienteId") Integer pacienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
