package com.hdc.hdc.service;

import com.hdc.hdc.infra.email.EmailService;
import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import com.hdc.hdc.repository.ProfissionalDaSaudeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ProfissionalDaSaudeService {

    private final ProfissionalDaSaudeRepository profissionalDaSaudeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public ProfissionalDaSaudeService(ProfissionalDaSaudeRepository profissionalDaSaudeRepository,
                                      PasswordEncoder passwordEncoder,
                                      EmailService emailService){
        this.profissionalDaSaudeRepository = profissionalDaSaudeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

        emailService.enviarEmaildeCadastro(
                profissionalDaSaude.getNome(),
                profissionalDaSaude.getEmail(),
                profissionalDaSaude.getUsername(),
                profissionalDaSaude.getSenha());

        profissionalDaSaude.setSenha(passwordEncoder.encode(profissionalDaSaude.getSenha()));
        profissionalDaSaude.setRole(Role.PROFISSIONAL_DA_SAUDE);
        profissionalDaSaude.setStatus(Status.ATIVO);

        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude visualizar(Integer id_profissional){

        return profissionalDaSaudeRepository.findById(id_profissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
    }

    public Page<ProfissionalDaSaude> visualizarTodos(Pageable pageable) {
        return profissionalDaSaudeRepository.findAll(pageable);
    }

    public ProfissionalDaSaude editar(ProfissionalDaSaude profissionalDaSaude, Integer id_profissional){

        ProfissionalDaSaude profissionalDaSaudeAtual = profissionalDaSaudeRepository.findById(id_profissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        profissionalDaSaude.setSenha(profissionalDaSaudeAtual.getSenha());
        profissionalDaSaude.setRole(profissionalDaSaudeAtual.getRole());
        profissionalDaSaude.setStatus(profissionalDaSaudeAtual.getStatus());
        profissionalDaSaude.setId(profissionalDaSaudeAtual.getId());

        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude ativar(Integer id_profissional){

        ProfissionalDaSaude profissionalDaSaude = profissionalDaSaudeRepository.findById(id_profissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        profissionalDaSaude.setStatus(Status.ATIVO);
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude inativar(Integer id_profissional){

        ProfissionalDaSaude profissionalDaSaude = profissionalDaSaudeRepository.findById(id_profissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        profissionalDaSaude.setStatus(Status.INATIVO);
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public Page<ProfissionalDaSaude> buscar(String nome, Pageable pageable){
        return profissionalDaSaudeRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }
}