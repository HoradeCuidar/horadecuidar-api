package com.hdc.hdc.prescricao_nutricional.adesao_refeicoes;

import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricional;
import com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "ocorrencia_refeicao",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ocorrencia_refeicao_data",
            columnNames = {
                    "refeicao_id",
                    "data_prevista"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcorrenciaRefeicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id", nullable = false)
    private PrescricaoNutricional prescricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id", nullable = false)
    private Refeicao refeicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcao_id")
    private OpcaoRefeicao opcao;

    @Column(name = "data_prevista", nullable = false)
    private LocalDate dataPrevista;

    @Column(name = "ordem_no_dia", nullable = false)
    private Integer ordemNoDia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAdesao status = StatusAdesao.PENDENTE;

    @Column(name = "data_hora_registro")
    private LocalDateTime dataHoraRegistro;

    @Column(columnDefinition = "text")
    private String observacao;
}
