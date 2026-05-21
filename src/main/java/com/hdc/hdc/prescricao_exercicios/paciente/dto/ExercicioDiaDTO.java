package com.hdc.hdc.prescricao_exercicios.paciente.dto;

import com.hdc.hdc.prescricao_exercicios.enums.FrequenciaTipo;
import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
import com.hdc.hdc.prescricao_exercicios.enums.TipoExercicio;
import com.hdc.hdc.prescricao_exercicios.enums.UnidadeDuracao;
import lombok.Data;

import java.util.UUID;

/**
 * Representa um exercício que o paciente deve ou deveria realizar hoje.
 * Inclui o status de realização quando já houver registro no dia.
 */
@Data
public class ExercicioDiaDTO {

    private Long itemExercicioId;
    private UUID prescricaoId;
    private String nomeExercicio;
    private TipoExercicio tipoExercicio;
    private Integer frequenciaValor;
    private FrequenciaTipo frequenciaTipo;
    private Integer duracaoValor;
    private UnidadeDuracao unidadeDuracao;
    private Integer series;
    private Integer repeticoes;
    private String intensidade;
    private String observacao;

    /** Status de realização do dia atual — null se ainda não registrado. */
    private StatusRealizacao statusHoje;

    /** ID do registro de realização do dia, se existir. */
    private Long realizacaoId;
}
