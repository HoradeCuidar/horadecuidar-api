package com.hdc.hdc.prescricao_exercicios;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.prescricao_exercicios.item_exercicio.ItemExercicio;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Prescrição de atividade física emitida por um profissional de saúde.
 * <p>
 * Regras de negócio:
 * <ul>
 *   <li>Uma prescrição pertence a exatamente um paciente e é emitida por um profissional.</li>
 *   <li>Deve conter ao menos um {@link ItemExercicio}.</li>
 *   <li>A flag {@code ativo} permite desativar sem excluir, preservando histórico.</li>
 *   <li>Quando {@code dataFim} é nula, a prescrição é considerada de prazo indeterminado
 *       enquanto {@code ativo} for {@code true}.</li>
 * </ul>
 */
@Entity
@Table(name = "prescricao_exercicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Usuario profissional;

    @Column(name = "data_inicio", nullable = false)
    private Date dataInicio;

    @Column(name = "data_fim")
    private Date dataFim;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemExercicio> exercicios;
}
