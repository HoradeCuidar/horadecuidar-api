package com.hdc.hdc.profissionais_saude;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.infra.email.EmailMontagemService;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeSelfUpdateDTO;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfissionalDaSaudeService {

    private final ProfissionalDaSaudeRepository profissionalDaSaudeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailMontagemService emailMontagemService;
    private final R2Service r2Service;

    @Autowired
    public ProfissionalDaSaudeService(ProfissionalDaSaudeRepository profissionalDaSaudeRepository,
            PasswordEncoder passwordEncoder,
            EmailMontagemService emailMontagemService,
            R2Service r2Service) {
        this.profissionalDaSaudeRepository = profissionalDaSaudeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailMontagemService = emailMontagemService;
        this.r2Service = r2Service;
    }

    public ProfissionalDaSaude cadastrar(ProfissionalDaSaude profissionalDaSaude) {

        // PERSONALIZAR DEPOIS
        if (profissionalDaSaudeRepository.existsByEmail(profissionalDaSaude.getEmail())) {
            throw new ResourceNotFoundException("Já existe um profissional cadastrado com este e-mail");
        }

        // PERSONALIZAR DEPOIS
        if (profissionalDaSaudeRepository.existsByUsername(profissionalDaSaude.getUsername())) {
            throw new ResourceNotFoundException("Já existe um profissional cadastrado com este username");
        }

        emailMontagemService.enviarConfirmacaoCadastro(
                profissionalDaSaude.getNome(),
                profissionalDaSaude.getEmail(),
                profissionalDaSaude.getUsername(),
                profissionalDaSaude.getSenha());

        profissionalDaSaude.setSenha(passwordEncoder.encode(profissionalDaSaude.getSenha()));
        profissionalDaSaude.setRole(Role.PROFISSIONAL_DA_SAUDE);
        profissionalDaSaude.setStatus(Status.ATIVO);

        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude visualizar(Integer profissionalId) {

        return profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID informado."));
    }

    public Page<ProfissionalDaSaude> visualizarTodos(Pageable pageable) {
        return profissionalDaSaudeRepository.findAll(pageable);
    }

    public ProfissionalDaSaude editar(ProfissionalDaSaude profissionalDaSaude, Integer profissionalId) {

        ProfissionalDaSaude profissionalDaSaudeAtual = profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com o ID informado"));

        profissionalDaSaude.setSenha(profissionalDaSaudeAtual.getSenha());
        profissionalDaSaude.setRole(profissionalDaSaudeAtual.getRole());
        profissionalDaSaude.setStatus(profissionalDaSaudeAtual.getStatus());
        profissionalDaSaude.setId(profissionalDaSaudeAtual.getId());

        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude editarPerfil(ProfissionalDaSaudeSelfUpdateDTO dto, Integer profissionalId) {
        ProfissionalDaSaude profissionalDaSaudeAtual = profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado."));

        if (!profissionalDaSaudeAtual.getEmail().equalsIgnoreCase(dto.getEmail()) 
                && profissionalDaSaudeRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceNotFoundException("Já existe um profissional cadastrado com este e-mail");
        }

        profissionalDaSaudeAtual.setNome(dto.getNome());
        profissionalDaSaudeAtual.setEmail(dto.getEmail());
        profissionalDaSaudeAtual.setTelefone(dto.getTelefone());
        profissionalDaSaudeAtual.setGenero(dto.getGenero());
        profissionalDaSaudeAtual.setDataDeNascimento(dto.getDataDeNascimento());
        profissionalDaSaudeAtual.setRua(dto.getRua());
        profissionalDaSaudeAtual.setBairro(dto.getBairro());
        profissionalDaSaudeAtual.setEstado(dto.getEstado());
        profissionalDaSaudeAtual.setCidade(dto.getCidade());
        profissionalDaSaudeAtual.setNumeroDaCasa(dto.getNumeroDaCasa());

        return profissionalDaSaudeRepository.save(profissionalDaSaudeAtual);
    }

    public ProfissionalDaSaude ativar(Integer profissionalId) {

        ProfissionalDaSaude profissionalDaSaude = profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado."));

        profissionalDaSaude.setStatus(Status.ATIVO);
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public ProfissionalDaSaude inativar(Integer profissionalId) {

        ProfissionalDaSaude profissionalDaSaude = profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado."));

        profissionalDaSaude.setStatus(Status.INATIVO);
        return profissionalDaSaudeRepository.save(profissionalDaSaude);
    }

    public Page<ProfissionalDaSaude> buscar(String nome, Pageable pageable) {
        return profissionalDaSaudeRepository.findByNomeContainingIgnoreCase(nome, pageable);
    }

    public String uploadFotoDePerfil(Integer profissionalId, MultipartFile fotoDePerfil) throws IOException {

        ProfissionalDaSaude profissionalDaSaude = profissionalDaSaudeRepository.findById(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        String url = r2Service.upload(fotoDePerfil);
        if (profissionalDaSaude.getFotoDePerfil().isEmpty()) {
            profissionalDaSaude.setFotoDePerfil(url);
        } else {
            r2Service.delete(profissionalDaSaude.getFotoDePerfil());
            profissionalDaSaude.setFotoDePerfil(url);
        }

        profissionalDaSaudeRepository.save(profissionalDaSaude);
        return url;
    }
}