package com.hdc.hdc.prescricao_exercicios.item_exercicio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdc.hdc.prescricao_exercicios.PrescricaoExercicio;
import com.hdc.hdc.prescricao_exercicios.enums.FrequenciaTipo;
import com.hdc.hdc.prescricao_exercicios.enums.TipoExercicio;
import com.hdc.hdc.prescricao_exercicios.enums.UnidadeDuracao;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_exercicio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescricao_id")
    @JsonIgnore
    private PrescricaoExercicio prescricao;

    @Column(name = "nome_exercicio", nullable = false)
    private String nomeExercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_exercicio", nullable = false)
    private TipoExercicio tipoExercicio;

    @Column(name = "frequencia_valor", nullable = false)
    private Integer frequenciaValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequencia_tipo", nullable = false)
    private FrequenciaTipo frequenciaTipo;

    @Column(name = "duracao_valor", nullable = false)
    private Integer duracaoValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_duracao", nullable = false)
    private UnidadeDuracao unidadeDuracao;

    @Column(name = "series")
    private Integer series;

    @Column(name = "repeticoes")
    private Integer repeticoes;

    @Column(name = "intensidade")
    private String intensidade;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
