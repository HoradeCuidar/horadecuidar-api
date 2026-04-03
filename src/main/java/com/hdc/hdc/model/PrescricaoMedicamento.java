package com.hdc.hdc.model;

import com.hdc.hdc.model.associacoes.ItemMedicacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
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
    private ProfissionalDaSaude profissional;

    @Column(name = "data_inicio")
    private Date dataInicio;

    @Column(name = "data_fim")
    private Date dataFim;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "ativo")
    private boolean ativo = true;

    @OneToMany(mappedBy = "prescricao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMedicacao> medicacoes;

}
