package com.hdc.hdc.prescricao_medicamentos.dto;

import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescricaoMedicamentoRequestDTO {

    @NotNull(message = "A data de início é obrigatória.")
    private LocalDate dataInicio;

    @NotNull(message = "A data final é obrigatória.")
    private LocalDate dataFim;

    private String observacao;

    @NotEmpty(message = "A prescrição deve conter ao menos um medicamento.")
    @Valid
    private List<ItemMedicacaoDTO> medicacoes;

}
