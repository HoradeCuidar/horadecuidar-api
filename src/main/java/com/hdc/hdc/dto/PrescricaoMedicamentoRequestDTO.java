package com.hdc.hdc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PrescricaoMedicamentoRequestDTO {

    @NotNull(message = "A data de início é obrigatória.")
    private Date dataInicio;

    private Date dataFim;

    private String observacao;

    @NotEmpty(message = "A prescrição deve conter ao menos um medicamento.")
    @Valid
    private List<ItemMedicacaoDTO> medicacoes;

}
