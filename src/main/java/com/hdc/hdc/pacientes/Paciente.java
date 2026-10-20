package com.hdc.hdc.pacientes;

import com.hdc.hdc.pacientes.associacoes.PacienteDoencas;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Genero;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;
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
                    Integer idade,
                    Role role,
                    Status status,
                    String telefone,
                    String rua,
                    String bairro,
                    String estado,
                    String cidade,
                    String numeroDaCasa,
                    Genero genero,
                    String email,
                    String fotoDePerfil){
        super(id, nome, username, senha, dataDeNascimento, idade, role, status, telefone, rua, bairro, estado, cidade, numeroDaCasa, genero, email, fotoDePerfil);
    }
}