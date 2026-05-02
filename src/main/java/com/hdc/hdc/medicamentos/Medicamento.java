package com.hdc.hdc.medicamentos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medicamentos")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", unique = true, length = 255)
    private String nome;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    public Medicamento(String nome) {
        this.nome = nome;
        this.criadoEm = LocalDateTime.now();
    }
}
