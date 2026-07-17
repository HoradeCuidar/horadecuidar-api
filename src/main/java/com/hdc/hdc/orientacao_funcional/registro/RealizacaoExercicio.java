package com.hdc.hdc.orientacao_funcional.registro;

import com.hdc.hdc.orientacao_funcional.orientacao.OrientacaoFuncional;
import com.hdc.hdc.pacientes.Paciente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "realizacao_exercicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealizacaoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientacao_funcional_id", nullable = false)
    private OrientacaoFuncional orientacaoFuncional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRealizacao status;

    @Column(name = "duracao_realizada_minutos")
    private Integer duracaoRealizadaMinutos;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensacao_final")
    private SensacaoFinal sensacaoFinal;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;
}

