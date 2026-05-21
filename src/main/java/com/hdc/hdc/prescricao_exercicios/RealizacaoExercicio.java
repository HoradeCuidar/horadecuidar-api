package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicio;
import com.hdc.hdc.prescricao_exercicios.enums.StatusRealizacao;
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

import java.util.Date;

/**
 * Registro de realização de um item de exercício pelo paciente.
 * <p>
 * Regras de negócio:
 * <ul>
 *   <li>Um registro pertence a exatamente um {@link ItemExercicio} e um {@link Paciente}.</li>
 *   <li>Deve haver no máximo um registro por item por dia — validação feita na camada de serviço.</li>
 *   <li>O paciente pode alterar o registro no mesmo dia da criação.</li>
 *   <li>{@code duracaoRealizadaMinutos} é opcional; permite registrar sessões parciais.</li>
 * </ul>
 */
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
    @JoinColumn(name = "item_exercicio_id", nullable = false)
    private ItemExercicio itemExercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRealizacao status;

    /** Duração real da sessão em minutos. Opcional — para registros parciais. */
    @Column(name = "duracao_realizada_minutos")
    private Integer duracaoRealizadaMinutos;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "data_registro", nullable = false)
    private Date dataRegistro;
}
