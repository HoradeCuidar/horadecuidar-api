package com.hdc.hdc.prescricao_medicamentos.associacoes;

import com.hdc.hdc.prescricao_medicamentos.enums.DosagemUnidade;
import com.hdc.hdc.prescricao_medicamentos.enums.IntervaloTipo;
import com.hdc.hdc.prescricao_medicamentos.enums.ViaAdministracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemMedicacaoDTO {

    @NotBlank(message = "O nome do medicamento é obrigatório.")
    private String nomeMedicamento;

    @NotNull(message = "O valor da dosagem é obrigatório.")
    private Double dosagemValor;

    @NotNull(message = "A unidade da dosagem é obrigatória.")
    private DosagemUnidade dosagemUnidade;

    @NotNull(message = "A quantidade de doses é obrigatória.")
    private Integer quantidadeDoses;

    @NotNull(message = "O valor do intervalo é obrigatório.")
    private Integer intervaloValor;

    @NotNull(message = "O tipo do intervalo é obrigatório.")
    private IntervaloTipo intervaloTipo;

    @NotNull(message = "A via de administração é obrigatória.")
    private ViaAdministracao viaAdministracao;

    private String observacao;
}
