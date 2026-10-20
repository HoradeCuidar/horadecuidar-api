package com.hdc.hdc.indicadores.relatorio.adesao;

import com.hdc.hdc.indicadores.relatorio.adesao.dto.DadosDetalhamentoDiarioNutricional;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.DadosEvolucaoSemanalNutricional;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.DadosResumoAdesaoNutricional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RelatorioAdesaoNutricionalRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public long contarDias(Integer pacienteId, LocalDate dataInicial, LocalDate dataFinal) {
        String sql = """
            SELECT COUNT(DISTINCT o.data_prevista)
            FROM ocorrencia_refeicao o
            INNER JOIN prescricao_nutricional p ON p.id = o.prescricao_id
            WHERE p.paciente_id = :pacienteId
              AND o.data_prevista BETWEEN :dataInicial AND :dataFinal
              AND o.status <> 'CANCELADO'
            """;
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("pacienteId", pacienteId)
                .addValue("dataInicial", dataInicial)
                .addValue("dataFinal", dataFinal);
        return jdbcTemplate.queryForObject(sql, parametros, Long.class);
    }

    public DadosResumoAdesaoNutricional buscarResumo(
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

            FROM ocorrencia_refeicao o

            INNER JOIN prescricao_nutricional p
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
                        new DadosResumoAdesaoNutricional(
                                resultado.getInt("esperado"),
                                resultado.getInt("realizado"),
                                resultado.getInt("nao_realizado"),
                                resultado.getInt("sem_registro")
                        )
        );
    }

    public List<DadosEvolucaoSemanalNutricional> buscarEvolucaoSemanal(
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

            FROM ocorrencia_refeicao o

            INNER JOIN prescricao_nutricional p
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
                        new DadosEvolucaoSemanalNutricional(
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

    public List<DadosDetalhamentoDiarioNutricional> buscarDetalhamentoDiario(
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

                FROM ocorrencia_refeicao o

                INNER JOIN prescricao_nutricional p
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

        long deslocamento = (long) pagina * tamanho;

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
                        new DadosDetalhamentoDiarioNutricional(
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
