package com.hdc.hdc.model;

import com.hdc.hdc.model.enums.Genero;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(name = "data_de_nascimento", nullable = false)
    private LocalDate dataDeNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, insertable = false, updatable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String rua;

    @Column(length = 50)
    private String bairro;

    @Column(length = 50)
    private String estado;

    @Column(length = 50)
    private String cidade;

    @Column(name = "numero_da_casa", length = 10)
    private String numeroDaCasa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Genero genero;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String fotoDePerfil;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == Status.ATIVO;
    }
}