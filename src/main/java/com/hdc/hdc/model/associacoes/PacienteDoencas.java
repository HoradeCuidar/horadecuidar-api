package com.hdc.hdc.model.associacoes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.model.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDoencas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "doenca_id")
    private Doenca doenca;

    private LocalDate dataDiagnostico;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;
}
