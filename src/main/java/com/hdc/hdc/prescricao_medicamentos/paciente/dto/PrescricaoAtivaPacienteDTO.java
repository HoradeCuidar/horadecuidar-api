package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PrescricaoAtivaPacienteDTO {
    private UUID id;
    private String nomeProfissional;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private List<ItemResumidoDTO> itens;

    @Data
    @Builder
    public static class ItemResumidoDTO {
        private Long itemId;
        private String nomeMedicamento;
        private String dosagemFormatada;
        private String frequencia;
        private String viaAdministracao;
    }
}
