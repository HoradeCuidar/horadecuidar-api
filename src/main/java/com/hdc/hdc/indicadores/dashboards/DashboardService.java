package com.hdc.hdc.indicadores.dashboards;

import com.hdc.hdc.indicadores.dashboards.dto.DashboardResumoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.DistribuicaoDoencaDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PacienteBaixaAdesaoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PrescricaoProximaVencimentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DIAS_PROXIMO_VENCIMENTO = 10;
    private static final int LIMITE_PACIENTES_BAIXA_ADESAO = 10;
    private static final int LIMITE_PRESCRICOES_VENCIMENTO = 10;
    private static final String ZONE = "America/Sao_Paulo";

    private final DashboardRepository dashboardRepository;

    @Transactional(readOnly = true)
    public DashboardResumoDTO buscarResumo() {

        LocalDate hoje = LocalDate.now(ZoneId.of(ZONE));

        return dashboardRepository.buscarResumo(
                hoje,
                hoje.plusDays(DIAS_PROXIMO_VENCIMENTO)
        );
    }

    @Transactional(readOnly = true)
    public List<PacienteBaixaAdesaoDTO>
    buscarPacientesBaixaAdesao() {

        return dashboardRepository
                .buscarPacientesBaixaAdesao(
                        LIMITE_PACIENTES_BAIXA_ADESAO
                );
    }

    @Transactional(readOnly = true)
    public List<PrescricaoProximaVencimentoDTO>
    buscarPrescricoesProximasVencimento() {

        LocalDate hoje = LocalDate.now(ZoneId.of(ZONE));

        return dashboardRepository
                .buscarPrescricoesProximasVencimento(
                        hoje,
                        hoje.plusDays(DIAS_PROXIMO_VENCIMENTO),
                        LIMITE_PRESCRICOES_VENCIMENTO
                );
    }

    @Transactional(readOnly = true)
    public List<DistribuicaoDoencaDTO>
    buscarDistribuicaoDoencas() {

        return dashboardRepository
                .buscarDistribuicaoDoencas();
    }
}