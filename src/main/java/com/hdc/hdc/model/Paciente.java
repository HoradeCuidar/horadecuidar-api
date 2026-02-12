package com.hdc.hdc.model;

import com.hdc.hdc.model.associacoes.PacienteDoencas;
import com.hdc.hdc.model.enums.Genero;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("PACIENTE")
@Entity
public class Paciente extends Usuario {

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PacienteDoencas> doencas = new ArrayList<>();

    @Column(columnDefinition = "text", name = "observacoes")
    private String observacoes;

    public Paciente(Integer id,
            String nome,
            String username,
            String senha,
            LocalDate dataDeNascimento,
            Role role,
            Status status,
            String telefone,
            String rua,
            String bairro,
            String estado,
            String cidade,
            String numeroDaCasa,
            Genero genero,
            String email) {
        super(id, nome, username, senha, dataDeNascimento, role, status, telefone, rua, bairro, estado, cidade,
                numeroDaCasa, genero, email);
    }
}