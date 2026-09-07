package com.hdc.hdc.indicadores.dashboards;

import com.hdc.hdc.indicadores.dashboards.dto.DashboardResumoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.DistribuicaoDoencaDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PacienteBaixaAdesaoDTO;
import com.hdc.hdc.indicadores.dashboards.dto.PrescricaoProximaVencimentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumo")
    public DashboardResumoDTO buscarResumo() {
        return dashboardService.buscarResumo();
    }

    @GetMapping("/pacientes-baixa-adesao")
    public List<PacienteBaixaAdesaoDTO>
    buscarPacientesBaixaAdesao() {
        return dashboardService.buscarPacientesBaixaAdesao();
    }

    @GetMapping("/prescricoes-proximas-vencimento")
    public List<PrescricaoProximaVencimentoDTO>
    buscarPrescricoesProximasVencimento() {
        return dashboardService
                .buscarPrescricoesProximasVencimento();
    }

    @GetMapping("/distribuicao-doencas")
    public List<DistribuicaoDoencaDTO>
    buscarDistribuicaoDoencas() {
        return dashboardService.buscarDistribuicaoDoencas();
    }
}
