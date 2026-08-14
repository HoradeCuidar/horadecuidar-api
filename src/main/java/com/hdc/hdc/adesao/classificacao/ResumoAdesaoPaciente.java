package com.hdc.hdc.adesao.classificacao;

import com.hdc.hdc.pacientes.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resumo_adesao_paciente")
public class ResumoAdesaoPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private LocalDate periodoInicio;
    private LocalDate periodoFim;

    private Integer esperado;
    private Integer realizado;
    private Integer naoRealizado;
    private Integer semRegistro;

    private BigDecimal percentual;

    @Enumerated(EnumType.STRING)
    private ClassificacaoAdesao classificacao;

    private LocalDateTime calculadoEm;
}
