package com.hdc.hdc.prescricao_nutricional.alimento;

import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "alimento_prescrito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AlimentoPrescrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcao_id", nullable = false)
    private OpcaoRefeicao opcao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(precision = 8, scale = 2)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UnidadeDeMedida unidade;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}