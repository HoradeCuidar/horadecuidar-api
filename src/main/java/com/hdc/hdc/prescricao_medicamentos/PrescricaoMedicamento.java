package com.hdc.hdc.prescricao_medicamentos;

import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.usuarios.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescricao_medicamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescricaoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private Usuario profissional;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "ativo")
    private boolean ativo = true;

    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMedicacao> medicacoes;
}
