package com.hdc.hdc.service;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.dto.response.PacienteResponseDto;
import com.hdc.hdc.mapper.PacienteMapper;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import com.hdc.hdc.repository.interfaces.IPacienteRepository;
import com.hdc.hdc.service.interfaces.IPacienteService;
import com.hdc.hdc.util.exception.ResourceWithSameNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {

    private final IPacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final PasswordEncoder passwordEncoder;

    public PacienteResponseDto cadastrar(PacienteCreateDto paciente) {
        Paciente entity = this.pacienteMapper.toEntity(paciente);
        this.validarUnicidade(entity);

        entity.setSenha(passwordEncoder.encode(entity.getSenha()));
        entity.setRole(Role.PACIENTE);
        entity.setStatus(Status.ATIVO);

        return this.pacienteMapper.toDto(this.pacienteRepository.save(entity));
    }

    public PacienteResponseDto visualizarPorId(Long id) {
        return this.pacienteMapper.toDto(this.pacienteRepository.encontrarPorId(id));
    }

    public PacienteResponseDto visualizarPorEmail(String email) {
        return this.pacienteMapper.toDto(this.pacienteRepository.encontrarPorEmail(email));
    }

    public Page<PacienteResponseDto> encontrarPorNome(String nome) {
        Pageable pageable = PageRequest.of(0, 20);
        var result = this.pacienteRepository.encontrarPorNome(nome, pageable);

        return result.map(pacienteMapper::toDto);
    }

    public Page<PacienteResponseDto> visualizarTodos(Integer pagina, Integer limite) {
        Pageable pageable = PageRequest.of(pagina, limite);
        var result = this.pacienteRepository.visualizarTodos(pageable);

        return result.map(pacienteMapper::toDto);
    }

    public void atualizar(PacienteCreateDto dto, Long id) {
        Paciente existente = pacienteRepository.encontrarPorId(id);
        Paciente entity = this.pacienteMapper.toEntity(dto);
        validarUnicidade(entity.getEmail(), Math.toIntExact(id));

        existente.setNome(entity.getNome());
        existente.setEmail(entity.getEmail());
        existente.setTelefone(entity.getTelefone());
        existente.setGenero(entity.getGenero());
        existente.setRua(entity.getRua());
        existente.setBairro(entity.getBairro());
        existente.setEstado(entity.getEstado());
        existente.setCidade(entity.getCidade());
        existente.setNumeroDaCasa(entity.getNumeroDaCasa());
        existente.setObservacoes(entity.getObservacoes());

        existente.getDoencas().clear();
        if (entity.getDoencas() != null) {
            existente.getDoencas().addAll(entity.getDoencas());
        }

        this.pacienteRepository.save(existente);
    }

    public void deletar(Long id) {
        this.pacienteRepository.deleteById(id);
    }

    public PacienteResponseDto alterarStatus(Long id) {
        Paciente paciente = this.pacienteRepository.encontrarPorId(id);
        paciente.setStatus(paciente.getStatus() == Status.ATIVO ? Status.INATIVO : Status.ATIVO);

        return this.pacienteMapper.toDto(this.pacienteRepository.save(paciente));
    }

    private void validarUnicidade(Paciente paciente) {
        if (this.pacienteRepository.existsByEmail(paciente.getEmail())) {
            throw new ResourceWithSameNameException("Email", "Já existe um paciente registrado com esse email.");
        }
    }

    private void validarUnicidade(String email, Integer id) {
        Paciente existente = this.pacienteRepository.encontrarPorEmail(email);
        if (!Objects.equals(existente.getId(), id)) {
            throw new ResourceWithSameNameException("Email", "Já existe um paciente registrado com esse email.");
        }
    }

}
