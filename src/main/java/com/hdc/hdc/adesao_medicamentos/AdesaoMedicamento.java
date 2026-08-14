package com.hdc.hdc.adesao_medicamentos;

import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_adesao_medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdesaoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id")
    private PrescricaoMedicamento prescricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_medicacao_id")
    private ItemMedicacao itemMedicacao;

    @Column(name = "data_hora_registro")
    private LocalDateTime dataHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusAdesao status;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
