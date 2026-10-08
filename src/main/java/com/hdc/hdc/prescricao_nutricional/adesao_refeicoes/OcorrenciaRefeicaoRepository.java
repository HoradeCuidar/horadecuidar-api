package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes;

import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OcorrenciaRefeicaoRepository extends JpaRepository<OcorrenciaRefeicao, Integer> {

    List<OcorrenciaRefeicao> findByPrescricaoId(Integer prescricaoId);

    @Query("""
            SELECT o FROM OcorrenciaRefeicao o
            JOIN FETCH o.prescricao p
            JOIN FETCH o.refeicao r
            LEFT JOIN FETCH o.opcao
            WHERE p.pacienteId = :pacienteId
              AND p.status = :status
              AND o.dataPrevista = :data
            ORDER BY o.ordemNoDia ASC
            """)
    List<OcorrenciaRefeicao> findDoDiaByPacienteAndPrescricaoStatus(
            @Param("pacienteId") Integer pacienteId,
            @Param("status") StatusPrescricao status,
            @Param("data") LocalDate data
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE OcorrenciaRefeicao o
            SET o.status = :novoStatus
            WHERE o.status = :statusAtual
              AND o.dataPrevista < :hoje
            """)
    int marcarPendentesExpiradas(
            @Param("statusAtual") StatusAdesao statusAtual,
            @Param("novoStatus") StatusAdesao novoStatus,
            @Param("hoje") LocalDate hoje
    );
}
