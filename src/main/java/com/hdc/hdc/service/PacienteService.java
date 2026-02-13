package com.hdc.hdc.service;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.dto.response.PacienteResponseDto;
import com.hdc.hdc.mapper.PacienteMapper;
import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.associacoes.PacienteDoencas;
import com.hdc.hdc.model.enums.Role;
import com.hdc.hdc.model.enums.Status;
import com.hdc.hdc.repository.DoencaRepository;
import com.hdc.hdc.repository.interfaces.IPacienteRepository;
import com.hdc.hdc.service.interfaces.IPacienteService;
import com.hdc.hdc.util.exception.ResourceWithSameNameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {

    private final IPacienteRepository pacienteRepository;
    private final DoencaRepository doencaRepository;
    private final PacienteMapper pacienteMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PacienteResponseDto cadastrar(PacienteCreateDto paciente) {
        Paciente entity = this.pacienteMapper.toEntity(paciente);
        this.validarUnicidade(entity);

        entity.setSenha(passwordEncoder.encode(entity.getSenha()));
        entity.setRole(Role.PACIENTE);
        entity.setStatus(Status.ATIVO);

        return this.pacienteMapper.toDto(this.pacienteRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PacienteResponseDto visualizarPorId(Long id) {
        return this.pacienteMapper.toDto(this.pacienteRepository.encontrarPorId(id));
    }

    @Transactional(readOnly = true)
    public PacienteResponseDto visualizarPorEmail(String email) {
        return this.pacienteMapper.toDto(this.pacienteRepository.encontrarPorEmail(email));
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponseDto> encontrarPorNome(String nome) {
        Pageable pageable = PageRequest.of(0, 20);
        var result = this.pacienteRepository.encontrarPorNome(nome, pageable);

        return result.map(pacienteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponseDto> visualizarTodos(Integer pagina, Integer limite) {
        Pageable pageable = PageRequest.of(pagina, limite);
        var result = this.pacienteRepository.visualizarTodos(pageable);

        return result.map(pacienteMapper::toDto);
    }

    @Transactional
    public void atualizar(PacienteCreateDto dto, Long id) {
        Paciente existente = pacienteRepository.encontrarPorId(id);
        validarUnicidade(dto.email(), Math.toIntExact(id));

        existente.setNome(dto.nome());
        existente.setEmail(dto.email());
        existente.setTelefone(dto.telefone());
        existente.setGenero(dto.genero());
        existente.setRua(dto.rua());
        existente.setBairro(dto.bairro());
        existente.setEstado(dto.estado());
        existente.setCidade(dto.cidade());
        existente.setNumeroDaCasa(dto.numeroDaCasa());
        existente.setObservacoes(dto.observacoes());

        // Atualizar as doenças
        if (dto.doencas() != null) {
            List<Doenca> doencas = doencaRepository.findAllById(dto.doencas());
            existente.getDoencas().clear();

            List<PacienteDoencas> novasRelacoes = doencas.stream()
                    .map(doenca -> {
                        PacienteDoencas pd = new PacienteDoencas();
                        pd.setPaciente(existente);
                        pd.setDoenca(doenca);
                        pd.setDoenca(doenca);
                        pd.setDataDiagnostico(LocalDate.now());
                        return pd;
                    })
                    .toList();

            existente.getDoencas().addAll(novasRelacoes);
        }
        log.info("Atualinzado paciente, processo finalizado.");
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
