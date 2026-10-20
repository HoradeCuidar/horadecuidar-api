package com.hdc.hdc.orientacao_funcional.orientacao;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "midia_orientacao") @Getter @Setter @NoArgsConstructor
public class MidiaOrientacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "orientacao_id", nullable = false)
    private OrientacaoFuncional orientacao;
    private int ordem;
    private Long passoId;
    private String url;
    private String legenda;
    private String textoAlternativo;
    private boolean capa;
}
