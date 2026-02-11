package com.hdc.hdc.repository.interfaces;

public interface IPacienteRepository {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
