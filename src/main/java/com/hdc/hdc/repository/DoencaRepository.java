package com.hdc.hdc.repository;

import com.hdc.hdc.model.Doenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoencaRepository extends JpaRepository<Doenca, Long> {
    Optional<Doenca> findByNome(String nome);

    boolean existsByNome(String nome);
}
