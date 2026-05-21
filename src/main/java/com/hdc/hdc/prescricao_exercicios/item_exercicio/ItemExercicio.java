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

/**
 * Item individual de exercício dentro de uma prescrição.
 * <p>
 * Regras de negócio:
 * <ul>
 *   <li>Um item define um exercício específico com sua frequência e intensidade.</li>
 *   <li>frequenciaValor + frequenciaTipo descrevem quantas vezes por período o exercício deve ser feito
 *       (ex.: 3 vezes por SEMANA).</li>
 *   <li>duracaoValor + unidadeDuracao definem o tempo de cada sessão (ex.: 30 MINUTOS).</li>
 *   <li>series e repeticoes são opcionais — aplicáveis a exercícios de resistência/força.</li>
 * </ul>
 */
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

    /** Nome legível do exercício (ex.: "Caminhada", "Musculação — supino"). */
    @Column(name = "nome_exercicio", nullable = false)
    private String nomeExercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_exercicio", nullable = false)
    private TipoExercicio tipoExercicio;

    /** Quantas vezes por período o exercício deve ser realizado. */
    @Column(name = "frequencia_valor", nullable = false)
    private Integer frequenciaValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequencia_tipo", nullable = false)
    private FrequenciaTipo frequenciaTipo;

    /** Duração de cada sessão. */
    @Column(name = "duracao_valor", nullable = false)
    private Integer duracaoValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_duracao", nullable = false)
    private UnidadeDuracao unidadeDuracao;

    /** Número de séries — aplicável a exercícios de força/resistência. */
    @Column(name = "series")
    private Integer series;

    /** Número de repetições por série — aplicável a exercícios de força/resistência. */
    @Column(name = "repeticoes")
    private Integer repeticoes;

    /** Intensidade em texto livre (ex.: "moderada", "60–70% FC máx."). */
    @Column(name = "intensidade")
    private String intensidade;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
