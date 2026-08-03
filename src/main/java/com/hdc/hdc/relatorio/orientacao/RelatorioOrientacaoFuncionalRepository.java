package com.hdc.hdc.relatorio.orientacao;

import com.hdc.hdc.relatorio.orientacao.dto.ResumoFuncionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class RelatorioOrientacaoFuncionalRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ResumoFuncionalDTO buscarResumo(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        String sql = """
        SELECT
            COUNT(*) AS quantidade_linhas,
            COALESCE(SUM(duracao_realizada_minutos), 0)
                AS total_tempo_realizado
        FROM realizacao_exercicio
        WHERE paciente_id = :pacienteId
          AND data_registro BETWEEN :dataInicial AND :dataFinal
        """;

        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("pacienteId", pacienteId)
                .addValue("dataInicial", dataInicial)
                .addValue("dataFinal", dataFinal);

        return jdbcTemplate.queryForObject(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new ResumoFuncionalDTO(
                                dataInicial,
                                dataFinal,
                                resultado.getInt("quantidade_linhas"),
                                resultado.getInt("total_tempo_realizado")
                        )
        );
    }
}
