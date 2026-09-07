package com.hdc.hdc.adesao.classificacao;

import com.hdc.hdc.adesao.classificacao.repositories.ConsultaAdesaoPacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClassificacaoAdesaoScheduler {

    private static final int DIAS_ANALISADOS = 14;

    private final ConsultaAdesaoPacienteRepository consultaRepository;
    private final ClassificacaoAdesaoService classificacaoAdesaoService;

    @Scheduled(
            cron = "0 10 0 * * *",
            zone = "America/Fortaleza"
    )
    public void atualizarClassificacoesDiarias() {
        LocalDate dataFinal = LocalDate.now().minusDays(1);
        LocalDate dataInicial = dataFinal.minusDays(DIAS_ANALISADOS - 1L);

        List<Integer> pacientes =
                consultaRepository.buscarPacientesParaRecalculo(
                        dataInicial,
                        dataFinal
        );

        log.info("Iniciando atualização diária da classificação de {} pacientes.", pacientes.size());

        for (Integer pacienteId : pacientes) {
            try {
                classificacaoAdesaoService.recalcular(pacienteId);
            } catch (Exception exception) {
                log.error("Erro ao recalcular classificação do paciente {}.", pacienteId,
                        exception
                );
            }
        }

        log.info("Atualização diária das classificações finalizada.");
    }
}
