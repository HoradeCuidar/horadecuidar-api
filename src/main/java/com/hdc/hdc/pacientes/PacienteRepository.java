package com.hdc.hdc.pacientes;

import com.hdc.hdc.usuarios.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    
    Optional<Paciente> findByEmail(String email);

    Optional<Paciente> findById(Integer id);

    Page<Paciente> findAllByNomeContainingIgnoreCaseAndRole(String nome, Role role, Pageable pageable);

    @Query("select p from Paciente p where p.role = 'PACIENTE'")
    Page<Paciente> findAllWithRelations(Pageable pageable);
}
