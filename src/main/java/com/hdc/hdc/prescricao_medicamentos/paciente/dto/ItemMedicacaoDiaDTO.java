package com.hdc.hdc.prescricao_medicamentos.paciente.dto;

import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemMedicacaoDiaDTO {
    private Long itemId;
    private UUID prescricaoId;
    private String nomeMedicamento;
    private String dosagemFormatada;
    private String frequencia;
    private String viaAdministracao;
    private String observacao;
    private StatusAdesao statusAdesaoHoje;
    private Long adesaoId;
    private int dosesEsperadasHoje;
    private int dosesRegistradasHoje;
}
