package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescricao_nutricional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoNutricional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    @Column(name = "profissional_id", nullable = false)
    private Integer profissionalId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "data_encerramento")
    private LocalDate dataEncerramento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPrescricao status;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Refeicao> refeicoes = new ArrayList<>();
}