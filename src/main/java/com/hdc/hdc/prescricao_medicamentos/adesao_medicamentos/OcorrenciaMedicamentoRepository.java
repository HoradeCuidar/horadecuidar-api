package com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos;

import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OcorrenciaMedicamentoRepository extends JpaRepository<OcorrenciaMedicamento, Long> {

    List<OcorrenciaMedicamento> findByPrescricaoId(UUID prescricaoId);

    List<OcorrenciaMedicamento> findAllByDataPrevista(LocalDate dataPrevista);

    boolean existsByPrescricaoId(UUID prescricaoId);

    boolean existsByPrescricaoIdAndStatusIn(UUID prescricaoId, List<StatusAdesao> statuses);

    void deleteByPrescricaoId(UUID prescricaoId);

    Optional<OcorrenciaMedicamento> findByItemMedicacaoIdAndDataPrevistaAndOrdemNoDia(
            Long itemMedicacaoId,
            LocalDate dataPrevista,
            Integer ordemNoDia
    );

    List<OcorrenciaMedicamento> findByItemMedicacaoIdAndDataPrevistaAndStatusOrderByOrdemNoDiaAsc(
            Long itemMedicacaoId,
            LocalDate dataPrevista,
            StatusAdesao status
    );

    List<OcorrenciaMedicamento> findByItemMedicacaoIdAndDataPrevistaOrderByOrdemNoDiaAsc(
            Long itemMedicacaoId,
            LocalDate dataPrevista
    );

    @Query("""
        SELECT a FROM OcorrenciaMedicamento a
        WHERE a.itemMedicacao.id = :itemMedicacaoId
              AND a.dataHoraRegistro >= :inicioDia
              AND a.dataHoraRegistro < :fimDia
        """)
    List<OcorrenciaMedicamento> findByItemMedicacaoIdAndDia(
            @Param("itemMedicacaoId") Long itemMedicacaoId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia);

    @Query("""
        SELECT a FROM OcorrenciaMedicamento a
        WHERE a.itemMedicacao.id = :itemMedicacaoId
              AND a.dataHoraRegistro >= :inicioDia
              AND a.dataHoraRegistro < :fimDia
        ORDER BY a.dataHoraRegistro DESC
        """)
    Optional<OcorrenciaMedicamento> findUltimaByItemMedicacaoIdAndDia(
            @Param("itemMedicacaoId") Long itemMedicacaoId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia);

    @Query("""
        SELECT a FROM OcorrenciaMedicamento a
        WHERE a.prescricao.paciente.id = :pacienteId
              AND a.dataHoraRegistro >= :inicio
              AND a.dataHoraRegistro < :fim
        """)
    List<OcorrenciaMedicamento> findByPacienteIdAndPeriodo(
            @Param("pacienteId") Integer pacienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    List<OcorrenciaMedicamento>
    findByPrescricaoIdAndDataPrevistaGreaterThanEqualAndStatus(
            UUID prescricaoId,
            LocalDate dataPrevista,
            StatusAdesao status
    );

    boolean existsByItemMedicacaoIdAndDataPrevistaAndOrdemNoDiaAndStatusNot(
            Long itemMedicacaoId,
            LocalDate dataPrevista,
            Integer ordemNoDia,
            StatusAdesao status
    );

    @Query("""
    select o
    from OcorrenciaMedicamento o
    where o.prescricao.id = :prescricaoId
      and o.dataPrevista >= :dataInicial
      and o.status = :status
    """)
    List<OcorrenciaMedicamento> buscarOcorrenciasFuturas(
            @Param("prescricaoId") UUID prescricaoId,
            @Param("dataInicial") LocalDate dataInicial,
            @Param("status") StatusAdesao status
    );
}
