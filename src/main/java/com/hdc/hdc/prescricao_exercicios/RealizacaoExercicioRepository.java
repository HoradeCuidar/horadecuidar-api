package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RealizacaoExercicioRepository extends JpaRepository<RealizacaoExercicio, Long> {

    // Busca o registro de realização de um item em uma data específica para evitar duplicatas.
    @Query("""
        SELECT r FROM RealizacaoExercicio r
        WHERE r.itemExercicio.id = :itemId
              AND r.paciente.id = :pacienteId
              AND FUNCTION('DATE', r.dataRegistro) = FUNCTION('DATE', :data)
        """)
    Optional<RealizacaoExercicio> findByItemAndPacienteAndDia(
            @Param("itemId") Long itemId,
            @Param("pacienteId") Integer pacienteId,
            @Param("data") Date data);

    // Retorna todos os registros de um paciente em um intervalo de datas (para histórico).
    @Query("""
        SELECT r FROM RealizacaoExercicio r
        WHERE r.paciente.id = :pacienteId
              AND r.dataRegistro >= :inicio
              AND r.dataRegistro <= :fim
        ORDER BY r.dataRegistro DESC
        """)
    List<RealizacaoExercicio> findByPacienteIdAndPeriodo(
            @Param("pacienteId") Integer pacienteId,
            @Param("inicio") Date inicio,
            @Param("fim") Date fim);

    List<RealizacaoExercicio> findByItemExercicioId(Long itemExercicioId);

    long countByItemExercicioIdAndStatus(Long itemExercicioId, StatusRealizacao status);
}
