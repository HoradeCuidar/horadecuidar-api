package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.relatorio.adesao.dto.DadosResumoAdesaoMedicamento;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class RelatorioAdesaoMedicamentoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DadosResumoAdesaoMedicamento buscarResumo(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        String sql = """
            SELECT
                COUNT(*) AS esperado,

                COUNT(*) FILTER (
                    WHERE o.status = 'REALIZADO'
                ) AS realizado,

                COUNT(*) FILTER (
                    WHERE o.status = 'NAO_REALIZADO'
                ) AS nao_realizado,

                COUNT(*) FILTER (
                    WHERE o.status = 'PENDENTE'
                ) AS sem_registro

            FROM ocorrencia_medicamento o

            INNER JOIN prescricao_medicamento p
                ON p.id = o.prescricao_id

            WHERE p.paciente_id = :pacienteId
              AND o.data_prevista BETWEEN :dataInicial AND :dataFinal
              AND o.status <> 'CANCELADO'
            """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("pacienteId", pacienteId)
                        .addValue("dataInicial", dataInicial)
                        .addValue("dataFinal", dataFinal);

        return jdbcTemplate.queryForObject(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new DadosResumoAdesaoMedicamento(
                                resultado.getInt("esperado"),
                                resultado.getInt("realizado"),
                                resultado.getInt("nao_realizado"),
                                resultado.getInt("sem_registro")
                        )
        );
    }
}
