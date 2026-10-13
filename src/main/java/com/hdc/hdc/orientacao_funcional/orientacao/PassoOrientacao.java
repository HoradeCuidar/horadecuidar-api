package com.hdc.hdc.orientacao_funcional.orientacao;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "passo_orientacao") @Getter @Setter @NoArgsConstructor
public class PassoOrientacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "orientacao_id", nullable = false)
    private OrientacaoFuncional orientacao;
    private int ordem;
    @Column(columnDefinition="text")
    private String instrucao;
}
