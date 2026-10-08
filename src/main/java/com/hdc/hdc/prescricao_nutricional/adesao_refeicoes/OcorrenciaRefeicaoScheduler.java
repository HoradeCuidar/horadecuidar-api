package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcorrenciaRefeicaoScheduler {

    private static final String ZONE = "America/Fortaleza";

    private final OcorrenciaRefeicaoService ocorrenciaRefeicaoService;

    @Scheduled(
            cron = "0 5 0 * * *",
            zone = ZONE
    )
    public void finalizarOcorrenciasPendentesExpiradas() {
        LocalDate hoje = LocalDate.now(ZoneId.of(ZONE));

        log.info("Iniciando finalização diária de ocorrências nutricionais pendentes anteriores a {}.", hoje);

        try {
            int atualizadas = ocorrenciaRefeicaoService.finalizarOcorrenciasPendentesExpiradas(hoje);
            log.info("Finalização diária de ocorrências nutricionais concluída. {} registro(s) atualizado(s).",
                    atualizadas);
        } catch (Exception exception) {
            log.error("Erro ao finalizar ocorrências nutricionais pendentes expiradas.", exception);
        }
    }
}
