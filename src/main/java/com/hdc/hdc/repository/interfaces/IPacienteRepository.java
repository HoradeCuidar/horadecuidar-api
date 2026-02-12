package com.hdc.hdc.repository.interfaces;

import com.hdc.hdc.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPacienteRepository {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void deleteById(Long id);
    Paciente save(Paciente paciente);
    Paciente encontrarPorId(Long id);
    Paciente encontrarPorEmail(String email);
    Page<Paciente> encontrarPorNome(String nome, Pageable pageable);
    Page<Paciente> visualizarTodos(Pageable pageable);
}
