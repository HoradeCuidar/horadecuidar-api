package com.hdc.hdc.orientacao_funcional.orientacao;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="historico_orientacao") @Getter @Setter @NoArgsConstructor
public class HistoricoOrientacao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long orientacaoId;
 private Integer autorId;
 private LocalDateTime dataAlteracao;
 private String acao;
 @Column(columnDefinition="text") private String conteudoAnterior;
}
