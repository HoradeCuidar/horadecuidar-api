package com.hdc.hdc.adesao.classificacao.repositories;

import com.hdc.hdc.adesao.classificacao.dto.DadosCalculoAdesaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConsultaAdesaoPacienteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DadosCalculoAdesaoDTO buscarDados(
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
                        new DadosCalculoAdesaoDTO(
                                resultado.getInt("esperado"),
                                resultado.getInt("realizado"),
                                resultado.getInt("nao_realizado"),
                                resultado.getInt("sem_registro")
                        )
        );
    }

    public List<Integer> buscarPacientesParaRecalculo(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        String sql = """
            SELECT DISTINCT paciente_id
            FROM (
            SELECT p.paciente_id
            FROM prescricao_medicamento p

            INNER JOIN ocorrencia_medicamento o
                ON o.prescricao_id = p.id

            INNER JOIN usuarios u
                ON u.id = p.paciente_id

            WHERE u.status = 'ATIVO'
              AND o.data_prevista BETWEEN :dataInicial AND :dataFinal
              AND o.status <> 'CANCELADO'

            UNION

            SELECT rap.paciente_id
            FROM resumo_adesao_paciente rap

            INNER JOIN usuarios u
                ON u.id = rap.paciente_id

            WHERE u.status = 'ATIVO'
              AND rap.classificacao <> 'DADOS_INSUFICIENTES'
            ) pacientes
        """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("dataInicial", dataInicial)
                        .addValue("dataFinal", dataFinal);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        resultado.getInt("paciente_id")
        );
    }
}
