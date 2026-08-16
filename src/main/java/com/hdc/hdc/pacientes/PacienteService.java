package com.hdc.hdc.pacientes;

import com.hdc.hdc.doencas.Doenca;
import com.hdc.hdc.doencas.DoencaRepository;
import com.hdc.hdc.pacientes.associacoes.PacienteDoencas;
import com.hdc.hdc.pacientes.dto.PacienteCreateDto;
import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.pacientes.dto.PacienteSelfUpdateDto;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.usuarios.enums.Status;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
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
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DoencaRepository doencaRepository;
    private final PacienteMapper pacienteMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.hdc.hdc.adesao.classificacao.repositories.ResumoAdesaoPacienteRepository resumoAdesaoPacienteRepository;

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
    public PacienteResponseDto visualizarPorId(Integer pacienteId) {
        Paciente paciente = this.encontrarPaciente(pacienteId);
        var base = this.pacienteMapper.toDto(paciente);
        var resumo = resumoAdesaoPacienteRepository.findByPacienteId(pacienteId);
        return buildWithClassificacao(base, resumo.orElse(null));
    }

    @Transactional(readOnly = true)
    public PacienteResponseDto visualizarPorEmail(String email) {
        Paciente paciente = this.encontrarPorEmail(email);
        var base = this.pacienteMapper.toDto(paciente);
        var resumo = resumoAdesaoPacienteRepository.findByPacienteId(paciente.getId());
        return buildWithClassificacao(base, resumo.orElse(null));
    }
    
    @Transactional
    public PacienteResponseDto visualizarPerfil(Integer pacienteId) {
        Paciente paciente = this.encontrarPaciente(pacienteId);
        var base = this.pacienteMapper.toDto(paciente);
        var resumo = resumoAdesaoPacienteRepository.findByPacienteId(pacienteId);
        return buildWithClassificacao(base, resumo.orElse(null));
    }

    @Transactional(readOnly = true)
    public PacienteResponseDto visualizarPorEmail(Usuario usuario) {
        return this.pacienteMapper.toDto(this.encontrarPaciente(usuario.getId()));
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponseDto> encontrarPorNome(String nome) {
        Pageable pageable = PageRequest.of(0, 20);
        var result = this.encontrarPorNome(nome, pageable);

        return result.map(pacienteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponseDto> visualizarTodos(Integer pagina, Integer limite) {
        Pageable pageable = PageRequest.of(pagina, limite);
        var result = this.visualizarTodos(pageable);

        // obter resumos em lote para evitar N+1
        var pacientes = result.getContent();
        var ids = pacientes.stream().map(Paciente::getId).toList();
        var resumos = resumoAdesaoPacienteRepository.findByPacienteIdIn(ids);

        return result.map(p -> {
            var base = pacienteMapper.toDto(p);
            var resumo = resumos.stream()
                    .filter(r -> r.getPaciente() != null && r.getPaciente().getId().equals(p.getId()))
                    .max((a, b) -> a.getCalculadoEm().compareTo(b.getCalculadoEm()))
                    .orElse(null);
            return buildWithClassificacao(base, resumo);
        });
    }

    @Transactional
    public void atualizar(PacienteCreateDto dto, Integer id) {
        Paciente existente = encontrarPaciente(id);
        this.validarUnicidade(dto.email(), id);

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
                        pd.setDataDiagnostico(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
                        return pd;
                    })
                    .toList();

            existente.getDoencas().addAll(novasRelacoes);
        }
        log.info("Atualizando paciente, processo finalizado.");
    }

    @Transactional
    public PacienteResponseDto atualizarPerfil(PacienteSelfUpdateDto dto, Integer id) {
        Paciente existente = this.pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID", "Paciente não encontrado com id informado."));

        this.validarUnicidade(dto.email(), id);

        existente.setNome(dto.nome());
        existente.setEmail(dto.email());
        existente.setTelefone(dto.telefone());
        existente.setGenero(dto.genero());
        existente.setRua(dto.rua());
        existente.setBairro(dto.bairro());
        existente.setEstado(dto.estado());
        existente.setCidade(dto.cidade());
        existente.setNumeroDaCasa(dto.numeroDaCasa());
        existente.setDataDeNascimento(dto.dataDeNascimento());

        log.info("Atualizado perfil do paciente com id: {}", id);
        return this.pacienteMapper.toDto(this.pacienteRepository.save(existente));
    }


    public void deletar(Integer id) {
        this.pacienteRepository.deleteById(id);
    }

    public PacienteResponseDto alterarStatus(Integer id) {
        Paciente paciente = this.encontrarPaciente(id);
        paciente.setStatus(paciente.getStatus() == Status.ATIVO ? Status.INATIVO : Status.ATIVO);

        return this.pacienteMapper.toDto(this.pacienteRepository.save(paciente));
    }

    private void validarUnicidade(Paciente paciente) {
        if (this.pacienteRepository.existsByEmail(paciente.getEmail())) {
            throw new ResourceWithSameNameException("Email", "Já existe um paciente registrado com esse email.");
        }
    }

    private void validarUnicidade(String email, Integer id) {
        Paciente existente = this.pacienteRepository.findByEmail(email).orElse(null);
        if (existente != null && !existente.getId().equals(Math.toIntExact(id))) {
            throw new ResourceWithSameNameException("Email", "Já existe um paciente registrado com esse email.");
        }
    }

    // Métodos privados recorrentes
    private Paciente encontrarPaciente(Integer id) {
        return pacienteRepository
                .findByIdProjection(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID", "Paciente não encontrado com o id informado."));
    }

    private PacienteResponseDto buildWithClassificacao(PacienteResponseDto base, com.hdc.hdc.adesao.classificacao.ResumoAdesaoPaciente resumo) {
        com.hdc.hdc.adesao.classificacao.ClassificacaoAdesao classificacao = null;
        if (resumo != null) classificacao = resumo.getClassificacao();

        return new PacienteResponseDto(
                base.id(),
                base.nome(),
                base.email(),
                base.username(),
                classificacao,
                base.dataDeNascimento(),
                base.role(),
                base.status(),
                base.telefone(),
                base.rua(),
                base.bairro(),
                base.estado(),
                base.cidade(),
                base.numeroDaCasa(),
                base.genero(),
                base.doencas(),
                base.observacoes(),
                base.fotoDePerfil()
        );
    }

    private Paciente encontrarPorEmail(String email) {
        return pacienteRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email", "Paciente não encontrado com o email informado."));
    }

    private Page<Paciente> encontrarPorNome(String nome, Pageable pageable) {
        return pacienteRepository.findAllByNomeContainingIgnoreCaseAndRole(nome, Role.PACIENTE, pageable);
    }

    private Page<Paciente> visualizarTodos(Pageable pageable) {
        return pacienteRepository.findAllWithRelations(pageable);
    }
}
