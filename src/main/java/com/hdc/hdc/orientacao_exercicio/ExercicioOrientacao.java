package com.hdc.hdc.orientacao_exercicio;

import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "exercicios_orientacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExercicioOrientacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "text")
    private String finalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaExercicio categoria;

    @Column(columnDefinition = "text")
    private String descricao;

    @Column(name = "url_imagem")
    private String urlImagem;
}
