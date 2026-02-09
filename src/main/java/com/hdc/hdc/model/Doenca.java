package com.hdc.hdc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "doencas")
public class Doenca {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doencas_gen")
    @SequenceGenerator(
            name = "doencas_gen",
            sequenceName = "doencas_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "varchar(244)")
    private String nome;
}
