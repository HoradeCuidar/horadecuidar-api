package com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos;

import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "ocorrencia_medicamento",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ocorrencia_item_data_ordem",
            columnNames = {
                    "item_medicacao_id",
                    "data_prevista",
                    "ordem_no_dia"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OcorrenciaMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id")
    private PrescricaoMedicamento prescricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_medicacao_id")
    private ItemMedicacao itemMedicacao;

    private LocalDate dataPrevista;

    private Integer ordemNoDia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusAdesao status = StatusAdesao.PENDENTE;

    private LocalDateTime dataHoraRegistro;

    @Column(columnDefinition = "text")
    private String observacao;

    public boolean estaPendente() {
        return status == StatusAdesao.PENDENTE;
    }

    public boolean possuiRegistroDoPaciente() {
        return status == StatusAdesao.REALIZADO
                || status == StatusAdesao.NAO_REALIZADO;
    }

    public void cancelar() {
        if (!estaPendente()) {
            throw new IllegalStateException(
                    "Somente ocorrências pendentes podem ser canceladas."
            );
        }

        this.status = StatusAdesao.CANCELADO;
    }

    public void reativar() {
        if (status != StatusAdesao.CANCELADO) {
            throw new IllegalStateException(
                    "Somente ocorrências canceladas podem ser reativadas."
            );
        }

        this.status = StatusAdesao.PENDENTE;
    }
}
