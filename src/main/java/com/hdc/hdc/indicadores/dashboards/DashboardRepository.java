package com.hdc.hdc.indicadores.dashboards;

import com.hdc.hdc.indicadores.dashboards.dto.DashboardResumoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.DistribuicaoDoencaDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PacienteBaixaAdesaoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PrescricaoProximaVencimentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DashboardResumoDTO buscarResumo(
            LocalDate hoje,
            LocalDate limiteVencimento
    ) {
        String sql = """
                SELECT
                    (
                        SELECT COUNT(*)
                        FROM usuarios p
                        WHERE p.status = 'ATIVO' AND p.role = 'PACIENTE'
                    ) AS participantes_ativos,
                    (
                        SELECT COUNT(*)
                        FROM prescricao_medicamento pm
                        WHERE pm.ativo = true
                    ) AS prescricoes_ativas,
                    (
                        SELECT COUNT(*)
                        FROM prescricao_medicamento pm
                        WHERE pm.ativo = true
                            AND pm.data_fim BETWEEN :hoje AND :limiteVencimento
                    ) AS prescricoes_proximas_vencimento,
                    (
                        SELECT COUNT(*)
                        FROM resumo_adesao_paciente rap
                        WHERE rap.classificacao = 'BAIXA_ADESAO'
                    ) AS participantes_baixa_adesao
            """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("hoje", hoje)
                        .addValue(
                                "limiteVencimento",
                                limiteVencimento
                        );

        return jdbcTemplate.queryForObject(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new DashboardResumoDTO(
                                resultado.getInt("participantes_ativos"),
                                resultado.getInt("prescricoes_ativas"),
                                resultado.getInt("prescricoes_proximas_vencimento"),
                                resultado.getInt("participantes_baixa_adesao")
                        )
        );
    }

    public List<PacienteBaixaAdesaoDTO> buscarPacientesBaixaAdesao(
            int limite
    ) {
        String sql = """
            SELECT
                rap.paciente_id,
                u.nome,
                rap.percentual,
                rap.realizado,
                rap.esperado
    
            FROM resumo_adesao_paciente rap
    
            INNER JOIN usuarios p
                ON p.id = rap.paciente_id
    
            INNER JOIN usuarios u
                ON u.id = p.id
    
            WHERE rap.classificacao = 'BAIXA_ADESAO'
    
            ORDER BY rap.percentual ASC
    
            LIMIT :limite
        """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("limite", limite);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new PacienteBaixaAdesaoDTO(
                                resultado.getInt("paciente_id"),
                                resultado.getString("nome"),
                                resultado.getBigDecimal("percentual"),
                                resultado.getInt("realizado"),
                                resultado.getInt("esperado")
                        )
        );
    }

    public List<PrescricaoProximaVencimentoDTO>
    buscarPrescricoesProximasVencimento(
            LocalDate hoje,
            LocalDate limiteVencimento,
            int limite
    ) {
        String sql = """
            SELECT
                pm.id AS prescricao_id,
                u.id AS paciente_id,
                u.nome AS nome_paciente,
                pm.data_fim,
                pm.data_fim::date - CAST(:hoje AS date) AS dias_restantes

            FROM prescricao_medicamento pm

            INNER JOIN usuarios u
                ON u.id = pm.paciente_id

            WHERE pm.ativo = true
              AND pm.data_fim::date BETWEEN :hoje AND :limiteVencimento

            ORDER BY pm.data_fim ASC

            LIMIT :limite
        """;

        MapSqlParameterSource parametros =
                new MapSqlParameterSource()
                        .addValue("hoje", hoje)
                        .addValue(
                                "limiteVencimento",
                                limiteVencimento
                        )
                        .addValue("limite", limite);

        return jdbcTemplate.query(
                sql,
                parametros,
                (resultado, numeroLinha) ->
                        new PrescricaoProximaVencimentoDTO(
                                resultado.getObject(
                                        "prescricao_id",
                                        UUID.class
                                ),
                                resultado.getInt("paciente_id"),
                                resultado.getString("nome_paciente"),
                                resultado
                                        .getDate("data_fim")
                                        .toLocalDate(),
                                resultado.getInt("dias_restantes")
                        )
        );
    }

    public List<DistribuicaoDoencaDTO>
    buscarDistribuicaoDoencas() {

        String sql = """
            SELECT
                d.nome,
                COUNT(DISTINCT pd.paciente_id) AS quantidade
            FROM paciente_doencas pd

            INNER JOIN doencas d
                ON d.id = pd.doenca_id

            GROUP BY d.nome

            ORDER BY quantidade DESC
        """;

        return jdbcTemplate.query(
                sql,
                new MapSqlParameterSource(),
                (resultado, numeroLinha) ->
                        new DistribuicaoDoencaDTO(
                                resultado.getString("nome"),
                                resultado.getInt("quantidade")
                        )
        );
    }
}
