package com.hdc.hdc.prescricao_nutricional.modelo;

import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

@Getter @Setter
public class ModeloNutricionalResponseDTO {
    private Integer id;
    private String nome;
    private Integer referenciaKcal;
    private String descricao;
    private Integer versao;
    private Integer criadoPorId;
    private Instant criadoEm;
    private Instant atualizadoEm;
    private List<RefeicaoCreateDTO> refeicoes;
}
