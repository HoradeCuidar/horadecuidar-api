package com.hdc.hdc.repository;

import com.hdc.hdc.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
