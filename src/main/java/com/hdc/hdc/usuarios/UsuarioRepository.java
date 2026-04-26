package com.hdc.hdc.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    UserDetails findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}