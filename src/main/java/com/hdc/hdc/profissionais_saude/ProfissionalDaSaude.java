package com.hdc.hdc.profissionais_saude;

import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Genero;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("PROFISSIONAL_DA_SAUDE")
@Entity
public class ProfissionalDaSaude extends Usuario {

    public ProfissionalDaSaude(Integer id,
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