package com.hdc.hdc.relatorio.orientacao;

import com.hdc.hdc.orientacao_funcional.registro.SensacaoFinal;
import com.hdc.hdc.orientacao_funcional.registro.StatusRealizacao;
import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentoRealizacaoFuncionalDTO;
import com.hdc.hdc.relatorio.orientacao.dto.ResumoFuncionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    public List<DetalhamentoRealizacaoFuncionalDTO> buscarDetalhamentoDiarioFuncional(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            int pagina,
            int tamanho
    ) {
        String sql = """
        SELECT
            r.data_registro::date AS data,
            o.nome AS nome_orientacao,
            o.id AS id_orientacao,
            r.status AS status,
            r.duracao_realizada_minutos AS duracao,
            r.sensacao_final AS sensacao_final

        FROM realizacao_exercicio r

        INNER JOIN orientacao_funcional o
            ON o.id = r.orientacao_funcional_id

        WHERE r.paciente_id = :pacienteId
          AND r.data_registro >= :dataInicial
          AND r.data_registro < :dataFinalExclusiva

        ORDER BY
            r.data_registro DESC,
            r.id DESC

        LIMIT :tamanho
        OFFSET :deslocamento
        """;

        int deslocamento = pagina * tamanho;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("pacienteId", pacienteId)
                        .addValue("dataInicial", dataInicial.atStartOfDay())
                        .addValue("dataFinalExclusiva", dataFinal.plusDays(1).atStartOfDay())
                        .addValue("tamanho", tamanho)
                        .addValue("deslocamento", deslocamento);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new DetalhamentoRealizacaoFuncionalDTO((
                                resultado.getDate("data").toLocalDate()),
                                resultado.getString("nome_orientacao"),
                                resultado.getLong("id_orientacao"),
                                converterStatus(resultado.getString("status")),
                                resultado.getObject("duracao",Integer.class),
                                converterSensacaoFinal(resultado.getString("sensacao_final")
                                )
                        )
        );
    }

    private StatusRealizacao converterStatus(String valor) {
        if (valor == null) return null;

        return StatusRealizacao.valueOf(valor);
    }

    private SensacaoFinal converterSensacaoFinal(String valor) {
        if (valor == null) return null;

        return SensacaoFinal.valueOf(valor);
    }
}
