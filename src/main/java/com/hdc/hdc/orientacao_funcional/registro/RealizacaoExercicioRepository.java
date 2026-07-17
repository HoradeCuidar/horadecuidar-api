package com.hdc.hdc.orientacao_funcional.registro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RealizacaoExercicioRepository extends JpaRepository<RealizacaoExercicio, Long> {

    // Verifica se já existe registro da orientação para o paciente em um intervalo (mesmo dia)
    boolean existsByOrientacaoFuncionalIdAndPacienteIdAndDataRegistroBetween(
            Long orientacaoFuncionalId, Integer pacienteId, LocalDateTime inicio, LocalDateTime fim);

    // Retorna todos os registros de um paciente ordenados por data (para histórico)
    List<RealizacaoExercicio> findByPacienteIdOrderByDataRegistroDesc(Integer pacienteId);

    Page<RealizacaoExercicio> findAllByPacienteId(Pageable pageable, Integer pacienteId);

    // Retorna registros de um paciente em um intervalo de datas
    @Query("""
        SELECT r FROM RealizacaoExercicio r
        WHERE r.paciente.id = :pacienteId
              AND r.dataRegistro >= :inicio
              AND r.dataRegistro <= :fim
        ORDER BY r.dataRegistro DESC
        """)
    List<RealizacaoExercicio> findByPacienteIdAndPeriodo(
            @Param("pacienteId") Integer pacienteId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    long countByOrientacaoFuncionalIdAndStatus(Long orientacaoFuncionalId, StatusRealizacao status);
}

