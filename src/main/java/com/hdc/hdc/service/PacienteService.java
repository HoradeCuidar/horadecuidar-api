package com.hdc.hdc.service;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.mapper.PacienteMapper;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.repository.interfaces.IPacienteRepository;
import com.hdc.hdc.service.interfaces.IPacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {

    private final IPacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    public Paciente cadastrar(Paciente paciente) {
        return null;
    }

    public Paciente visualizar(Integer id_profissional) {
        return null;
    }

    public Page<Paciente> visualizarTodos(Pageable pageable) {
        return null;
    }

    public void atualizar(PacienteCreateDto paciente, Integer id) {

    }

    public void deletar(Integer id) {

    }

    public Paciente ativar(Integer id_profissional) {
        return null;
    }

    public Paciente inativar(Integer id_profissional) {
        return null;
    }
}
