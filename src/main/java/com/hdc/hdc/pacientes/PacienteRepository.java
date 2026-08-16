package com.hdc.hdc.pacientes;

import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.usuarios.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
            SELECT p
            FROM Paciente p
            LEFT JOIN FETCH p.doencas d
            WHERE p.email = :email
                AND p.role = com.hdc.hdc.usuarios.enums.Role.PACIENTE
    """)
    Optional<Paciente> findByEmail(@Param("email") String email);

    @Query("""
            SELECT p
            FROM Paciente p
            LEFT JOIN FETCH p.doencas d
            WHERE p.id = :pacienteId
                AND p.role = com.hdc.hdc.usuarios.enums.Role.PACIENTE
    """)
    Optional<Paciente> findByIdProjection(@Param("pacienteId") Integer pacienteId);

    @Query(value = """
        SELECT
            p.id AS id,
            p.nome AS nome,
            r.classificacao AS classificacao
        FROM usuarios p
        LEFT JOIN resumo_adesao_paciente r ON r.paciente_id = p.id
        WHERE p.role = 'PACIENTE'
    """, nativeQuery = true)
    List<PacienteResponseDto> findAllPacientes();

    Optional<Paciente> findById(Integer pacienteId);

    Page<Paciente> findAllByNomeContainingIgnoreCaseAndRole(String nome, Role role, Pageable pageable);

    @Query("select p from Paciente p where p.role = 'PACIENTE'")
    Page<Paciente> findAllWithRelations(Pageable pageable);
}
