package com.hdc.hdc.repository;

import com.hdc.hdc.model.ProfissionalDaSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfissionalDaSaudeRepository extends JpaRepository<ProfissionalDaSaude, Integer> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Page<ProfissionalDaSaude> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}