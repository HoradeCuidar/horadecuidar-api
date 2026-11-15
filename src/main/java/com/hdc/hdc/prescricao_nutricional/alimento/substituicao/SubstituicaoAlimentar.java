package com.hdc.hdc.prescricao_nutricional.alimento.substituicao;

import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescrito;
import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "substituicao_alimentar")
@Getter @Setter
public class SubstituicaoAlimentar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "alimento_id", nullable = false)
    private AlimentoPrescrito alimento;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal quantidade;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private UnidadeDeMedida unidade;
    @Column(name = "medida_caseira", columnDefinition = "TEXT")
    private String medidaCaseira;
    @Column(columnDefinition = "TEXT")
    private String observacao;
    @Column(nullable = false)
    private Integer ordem;
}
