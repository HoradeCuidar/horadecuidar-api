package com.hdc.hdc.service;

import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import com.hdc.hdc.repository.interfaces.IProfissionalDaSaudeRepository;
import com.hdc.hdc.service.interfaces.IProfissionalDaSaudeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfissionalDaSaudeService implements IProfissionalDaSaudeService {

    private final IProfissionalDaSaudeRepository profissionalDaSaudeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfissionalDaSaudeService(IProfissionalDaSaudeRepository profissionalDaSaudeRepository,
                                      PasswordEncoder passwordEncoder){
        this.profissionalDaSaudeRepository = profissionalDaSaudeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfissionalDaSaude cadastrar(ProfissionalDaSaude profissionalDaSaude){

        // PERSONALIZAR DEPOIS
        if (profissionalDaSaudeRepository.existsByEmail(profissionalDaSaude.getEmail())) {
            throw new RuntimeException("Já existe um profissional cadastrado com este e-mail");
        }

        // PERSONALIZAR DEPOIS
        if (profissionalDaSaudeRepository.existsByUsername(profissionalDaSaude.getUsername())) {
            throw new RuntimeException("Já existe um profissional cadastrado com este username");
        }

        profissionalDaSaude.setSenha(passwordEncoder.encode(profissionalDaSaude.getSenha()));
        profissionalDaSaude.setRole(Role.PROFISSIONAL_DA_SAUDE);
        profissionalDaSaude.setStatus(Status.ATIVO);

        // ADICIONAR O ENVIO DO E-MAIL
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }
}