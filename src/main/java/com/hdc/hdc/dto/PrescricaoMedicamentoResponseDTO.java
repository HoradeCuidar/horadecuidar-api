package com.hdc.hdc.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class PrescricaoMedicamentoResponseDTO {
    private UUID id;
    private Integer pacienteId;
    private Integer profissionalId;
    private String nomeProfissional;
    private Date dataInicio;
    private Date dataFim;
    private String observacao;
    private boolean ativo;
    private List<ItemMedicacaoDTO> medicacoes;
}
