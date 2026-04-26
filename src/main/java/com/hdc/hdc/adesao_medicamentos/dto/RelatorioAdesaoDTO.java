package com.hdc.hdc.adesao_medicamentos.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RelatorioAdesaoDTO {
    private UUID prescricaoId;
    private int totalDosesEsperadas;
    private int dosesRealizadas;
    private double percentualAdesao;
}
