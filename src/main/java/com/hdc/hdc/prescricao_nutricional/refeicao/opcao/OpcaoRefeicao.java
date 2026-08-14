package com.hdc.hdc.prescricao_nutricional.refeicao.opcao;

import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescrito;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opcao_refeicao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpcaoRefeicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id", nullable = false)
    private Refeicao refeicao;

    @Column(nullable = false)
    private Integer ordem;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @OneToMany(mappedBy = "opcao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlimentoPrescrito> alimentos = new ArrayList<>();
}