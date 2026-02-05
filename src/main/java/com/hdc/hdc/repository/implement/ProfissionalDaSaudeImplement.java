package com.hdc.hdc.repository.implement;

import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.repository.ProfissionalDaSaudeRepository;
import com.hdc.hdc.repository.interfaces.IProfissionalDaSaudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfissionalDaSaudeImplement implements IProfissionalDaSaudeRepository {

    private final ProfissionalDaSaudeRepository profissionalDaSaudeRepository;

    @Autowired
    public ProfissionalDaSaudeImplement(ProfissionalDaSaudeRepository profissionalDaSaudeRepository){
        this.profissionalDaSaudeRepository = profissionalDaSaudeRepository;
    }

    public ProfissionalDaSaude save(ProfissionalDaSaude profissionalDaSaude){
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public Optional<ProfissionalDaSaude> findById(Integer id){
        return profissionalDaSaudeRepository.findById(id);
    }

    public boolean existsByEmail(String email){
        return profissionalDaSaudeRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username){
        return profissionalDaSaudeRepository.existsByUsername(username);
    }

    public Page<ProfissionalDaSaude> findAll(Pageable pageable){
        return profissionalDaSaudeRepository.findAll(pageable);
    }
}