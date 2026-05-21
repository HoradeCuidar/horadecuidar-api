package com.hdc.hdc.prescricao_exercicios.item_exercicio;

import com.hdc.hdc.prescricao_exercicios.enums.FrequenciaTipo;
import com.hdc.hdc.prescricao_exercicios.enums.TipoExercicio;
import com.hdc.hdc.prescricao_exercicios.enums.UnidadeDuracao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de leitura e escrita para um item de exercício.
 * Usado tanto nas requisições do profissional quanto nas respostas ao paciente.
 */
@Data
public class ItemExercicioDTO {

    @NotBlank(message = "O nome do exercício é obrigatório.")
    private String nomeExercicio;

    @NotNull(message = "O tipo de exercício é obrigatório.")
    private TipoExercicio tipoExercicio;

    @NotNull(message = "O valor de frequência é obrigatório.")
    @Min(value = 1, message = "A frequência deve ser de ao menos 1.")
    private Integer frequenciaValor;

    @NotNull(message = "O tipo de frequência é obrigatório.")
    private FrequenciaTipo frequenciaTipo;

    @NotNull(message = "A duração da sessão é obrigatória.")
    @Min(value = 1, message = "A duração deve ser de ao menos 1.")
    private Integer duracaoValor;

    @NotNull(message = "A unidade de duração é obrigatória.")
    private UnidadeDuracao unidadeDuracao;

    /** Número de séries (opcional — para exercícios de força). */
    private Integer series;

    /** Número de repetições por série (opcional — para exercícios de força). */
    private Integer repeticoes;

    /** Intensidade em texto livre (opcional). Ex.: "moderada", "60–70% FC máx.". */
    private String intensidade;

    private String observacao;
}
