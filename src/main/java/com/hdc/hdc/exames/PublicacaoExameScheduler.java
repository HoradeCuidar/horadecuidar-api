package com.hdc.hdc.exames;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class PublicacaoExameScheduler {
    private final ExameRepository exames;
    private final ExameService service;
    public PublicacaoExameScheduler(ExameRepository exames, ExameService service) {
        this.exames = exames;
        this.service = service;
    }
    @Scheduled(fixedDelayString = "${exames.publicacao.intervalo-ms:60000}")
    public void executar() {
        exames.findByStatusAndDisponibilizacaoEmLessThanEqual(StatusExame.AGENDADO, Instant.now())
                .forEach(exame -> service.publicarAgendado(exame.getId()));
    }
}
