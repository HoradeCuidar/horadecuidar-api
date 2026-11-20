package com.hdc.hdc.prescricao_nutricional.modelo;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class ModeloNutricionalResumoDTO {
    private Integer id;
    private String nome;
    private Integer referenciaKcal;
    private String descricao;
    private Integer versao;
    private Instant atualizadoEm;
    private Integer quantidadeRefeicoes;
}
