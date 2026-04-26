package com.hdc.hdc.doencas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoencaRepository extends JpaRepository<Doenca, Long> {
    Optional<Doenca> findByNome(String nome);

    boolean existsByNome(String nome);
}
