package com.hdc.hdc.orientacao_funcional.tag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_funcional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagFuncional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(columnDefinition = "text")
    private String descricao;
}
