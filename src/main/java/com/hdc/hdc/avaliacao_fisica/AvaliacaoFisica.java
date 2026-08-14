package com.hdc.hdc.avaliacao_fisica;

import com.hdc.hdc.avaliacao_fisica.enums.FlexibilidadeFisica;
import com.hdc.hdc.avaliacao_fisica.enums.NivelAssimetria;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncional;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avaliacao_fisica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "forca_palmar_direita", precision = 5, scale = 2)
    private BigDecimal forcaPalmarDireita;

    @Column(name = "forca_palmar_esquerda", precision = 5, scale = 2)
    private BigDecimal forcaPalmarEsquerda;

    @Column(name = "assimetria_palmar", columnDefinition = "varchar(10)")
    private NivelAssimetria assimetriaPalmar;

    @Column(name = "forca_joelho_direita", precision = 5, scale = 2)
    private BigDecimal forcaJoelhoDireita;

    @Column(name = "forca_joelho_esquerda", precision = 5, scale = 2)
    private BigDecimal forcaJoelhoEsquerda;

    @Enumerated(EnumType.STRING)
    @Column(name = "assimetria_joelho")
    private NivelAssimetria assimetriaJoelho;

    @Column(name = "queixas", columnDefinition = "text")
    private String queixas;

    @Column(name = "observacoes", columnDefinition = "text")
    private String observacoesMusculoEsqueleticas;

    @Column(name = "orientacoes", columnDefinition = "text")
    private String orientacoesGerais;

    @ManyToMany
    @JoinTable(
            name = "avaliacao_indicacao_tag",
            joinColumns = @JoinColumn(name = "avaliacao_fisica_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_funcional_id")
    )
    private List<TagFuncional> indicacoesFuncionais = new ArrayList<>();

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}

