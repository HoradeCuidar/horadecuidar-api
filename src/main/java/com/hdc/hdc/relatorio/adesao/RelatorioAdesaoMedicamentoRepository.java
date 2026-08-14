package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.relatorio.adesao.dto.DadosDetalhamentoDiarioMedicamento;
import com.hdc.hdc.relatorio.adesao.dto.DadosEvolucaoSemanalMedicamento;
import com.hdc.hdc.relatorio.adesao.dto.DadosResumoAdesaoMedicamento;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    public List<DadosEvolucaoSemanalMedicamento> buscarEvolucaoSemanal(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        String sql = """
            SELECT
                DATE_TRUNC(
                    'week',
                    o.data_prevista
                )::date AS inicio_semana,

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

            GROUP BY
                DATE_TRUNC('week', o.data_prevista)

            ORDER BY
                inicio_semana
            """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("pacienteId", pacienteId)
                        .addValue("dataInicial", dataInicial)
                        .addValue("dataFinal", dataFinal);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new DadosEvolucaoSemanalMedicamento(
                                resultado
                                        .getDate("inicio_semana")
                                        .toLocalDate(),
                                resultado.getLong("esperado"),
                                resultado.getLong("realizado"),
                                resultado.getLong("nao_realizado"),
                                resultado.getLong("sem_registro")
                        )
        );
    }

    public List<DadosDetalhamentoDiarioMedicamento> buscarDetalhamentoDiario(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            int pagina,
            int tamanho
    ) {
        String sql = """
            WITH detalhamento AS (
                SELECT
                    o.data_prevista AS data,

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

                GROUP BY o.data_prevista
            )
            SELECT
                data,
                esperado,
                realizado,
                nao_realizado,
                sem_registro,
                COUNT(*) OVER () AS total_elementos

            FROM detalhamento

            ORDER BY data DESC

            LIMIT :tamanho
            OFFSET :deslocamento
            """;

        int deslocamento = pagina * tamanho;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("pacienteId", pacienteId)
                        .addValue("dataInicial", dataInicial)
                        .addValue("dataFinal", dataFinal)
                        .addValue("tamanho", tamanho)
                        .addValue("deslocamento", deslocamento);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new DadosDetalhamentoDiarioMedicamento(
                                resultado.getDate("data").toLocalDate(),
                                resultado.getLong("esperado"),
                                resultado.getLong("realizado"),
                                resultado.getLong("nao_realizado"),
                                resultado.getLong("sem_registro"),
                                resultado.getLong("total_elementos")
                        )
        );
    }
}
