package com.hdc.hdc.dto;

import com.hdc.hdc.model.enums.DosagemUnidade;
import com.hdc.hdc.model.enums.IntervaloTipo;
import com.hdc.hdc.model.enums.ViaAdministracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemMedicacaoDTO {

    private Long id;

    @NotBlank(message = "O nome do medicamento é obrigatório.")
    private String nomeMedicamento;

    @NotNull(message = "O valor da dosagem é obrigatório.")
    private Double dosagemValor;

    @NotNull(message = "A unidade da dosagem é obrigatória.")
    private DosagemUnidade dosagemUnidade;

    private Integer quantidadeDoses;

    @NotNull(message = "O valor do intervalo é obrigatório.")
    private Integer intervaloValor;

    @NotNull(message = "O tipo do intervalo é obrigatório.")
    private IntervaloTipo intervaloTipo;

    @NotNull(message = "A via de administração é obrigatória.")
    private ViaAdministracao viaAdministracao;

    private String observacao;
}
