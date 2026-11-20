package com.hdc.hdc.prescricao_nutricional.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "modelo_nutricional")
@Getter @Setter
public class ModeloNutricional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(name = "referencia_kcal")
    private Integer referenciaKcal;
    @Column(columnDefinition = "TEXT")
    private String descricao;
    @Column(name = "refeicoes_json", nullable = false, columnDefinition = "TEXT")
    private String refeicoesJson;
    @Column(nullable = false)
    private Integer versao;
    @Version
    @Column(nullable = false)
    private Integer revisao;
    @Column(name = "criado_por_id", nullable = false)
    private Integer criadoPorId;
    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}
