package com.hdc.hdc.repository.interfaces;

import com.hdc.hdc.model.ProfissionalDaSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IProfissionalDaSaudeRepository {

    ProfissionalDaSaude save(ProfissionalDaSaude profissionalDaSaude);
    Optional<ProfissionalDaSaude> findById(Integer id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Page<ProfissionalDaSaude> findAll(Pageable pageable);
}