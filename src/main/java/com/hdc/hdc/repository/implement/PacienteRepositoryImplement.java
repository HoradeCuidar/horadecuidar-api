package com.hdc.hdc.repository.implement;

import com.hdc.hdc.repository.PacienteRepository;
import com.hdc.hdc.repository.interfaces.IPacienteRepository;
import lombok.RequiredArgsConstructor;
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
}
