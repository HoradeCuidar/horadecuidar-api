package com.hdc.hdc.exames;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "exames")
@Getter @Setter
public class Exame {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;
    @Column(name = "profissional_cadastro_id", nullable = false)
    private Integer profissionalCadastroId;
    @Column(name = "data_coleta", nullable = false)
    private LocalDate dataColeta;
    private String laboratorio;
    private String observacao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExame status;
    @Column(name = "disponibilizacao_em")
    private Instant disponibilizacaoEm;
    @Column(name = "publicado_em")
    private Instant publicadoEm;
    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public boolean disponivelParaPaciente(Instant agora) {
        return status == StatusExame.PUBLICADO ||
                status == StatusExame.AGENDADO && !disponibilizacaoEm.isAfter(agora);
    }
}
