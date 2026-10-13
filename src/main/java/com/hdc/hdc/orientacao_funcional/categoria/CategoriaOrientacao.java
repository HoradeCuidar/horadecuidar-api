package com.hdc.hdc.orientacao_funcional.categoria;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @Table(name="categoria_orientacao")
public class CategoriaOrientacao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true, length=100) private String nome;
 private boolean ativo = true;
}
