package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.prescricao_exercicios.enums.FlexibilidadeFisica;
import com.hdc.hdc.prescricao_exercicios.enums.NivelAssimetria;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "avaliacao-fisica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;

    @Column(name = "realiza_atividade", nullable = false, columnDefinition = "boolean")
    private boolean realizaAtividadeFisica;

    @Column(name = "atividade_realizada", columnDefinition = "text")
    private String atividadeRealizada;

    @Column(name = "frequencia_semanal")
    private Integer frequenciaSemanal;

    @Enumerated(EnumType.STRING)
    @Column(name = "flexibilidade")
    private FlexibilidadeFisica flexibilidade;

    @Column(name = "forca_palmar_direita", columnDefinition = "varchar(10)")
    private String forcaPalmarDireita;

    @Column(name = "forca_palmar_esquerda", columnDefinition = "varchar(10)")
    private String forcaPalmarEsquerda;

    @Column(name = "assimetria_palmar", columnDefinition = "varchar(10)")
    private NivelAssimetria assimetriaPalmar;

    @Column(name = "forca_joelho_direita", columnDefinition = "varchar(10)")
    private String forcaJoelhoDireita;

    @Column(name = "forca_joelho_esquerda", columnDefinition = "varchar(10)")
    private String forcaJoelhoEsquerda;

    @Enumerated(EnumType.STRING)
    @Column(name = "assimetria_joelho")
    private NivelAssimetria assimetriaJoelho;

    @Column(name = "queixas", columnDefinition = "text")
    private String queixas;

    @Column(name = "observacoes", columnDefinition = "text")
    private String observacoesMusculoEsqueleticas;

    @Column(name = "orientacoes", columnDefinition = "text")
    private String orientacoesGerais;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;
}
