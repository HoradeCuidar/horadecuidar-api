package com.hdc.hdc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hdc.hdc.model.enums.StatusAdesao;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_adesao_medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAdesaoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescricao_id")
    private PrescricaoMedicamento prescricao;

    @Column(name = "data_hora_registro")
    private LocalDateTime dataHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusAdesao status;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
