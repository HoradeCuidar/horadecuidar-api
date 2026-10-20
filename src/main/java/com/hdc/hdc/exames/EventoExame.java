package com.hdc.hdc.exames;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "eventos_exame")
@Getter @Setter
public class EventoExame {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exame_id", nullable = false)
    private Long exameId;
    @Column(name = "autor_id")
    private Integer autorId;
    @Column(nullable = false)
    private String acao;
    @Column(name = "ocorrido_em", nullable = false)
    private Instant ocorridoEm;
    private String detalhes;
}
