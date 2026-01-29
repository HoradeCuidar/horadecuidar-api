package com.hdc.hdc.model;

import com.hdc.hdc.model.enums.Genero;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("ADMIN")
@Entity
public class Administrador extends Usuario{

    public Administrador(Integer id,
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
                         String email){
        super(id, nome, username, senha, dataDeNascimento, role, status, telefone, rua, bairro, estado, cidade, numeroDaCasa, genero, email);
    }
}