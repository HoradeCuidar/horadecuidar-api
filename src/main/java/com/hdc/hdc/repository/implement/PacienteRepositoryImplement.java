package com.hdc.hdc.repository.implement;

import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.repository.PacienteRepository;
import com.hdc.hdc.repository.interfaces.IPacienteRepository;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PacienteRepositoryImplement implements IPacienteRepository {

    private final PacienteRepository pacienteRepository;

    public boolean existsByEmail(String email) {
        return pacienteRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return pacienteRepository.existsByUsername(username);
    }

    public void deleteById(Long id) {
        pacienteRepository.deleteById(id);
    }

    public Paciente save(Paciente paciente) {
        pacienteRepository.save(paciente);
        return pacienteRepository
                .findByEmail(paciente.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email", "Paciente não encontrado com o email informado (no save)."));
    }

    public Paciente encontrarPorId(Long id) {
        return pacienteRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email", "Paciente não encontrado com o id informado."));
    }

    public Paciente encontrarPorEmail(String email) {
        return pacienteRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email", "Paciente não encontrado com o email informado."));
    }

    public Page<Paciente> encontrarPorNome(String nome, Pageable pageable) {
        return pacienteRepository.findAllByNomeContainingIgnoreCaseAndRole(nome, Role.PACIENTE, pageable);
    }

    public Page<Paciente> visualizarTodos(Pageable pageable) {
        return pacienteRepository.findAllWithRelations(pageable);
    }
}
