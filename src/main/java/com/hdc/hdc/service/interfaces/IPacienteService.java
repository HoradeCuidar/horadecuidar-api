package com.hdc.hdc.service.interfaces;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPacienteService {
    Paciente cadastrar(Paciente paciente);
    Paciente visualizar(Integer id_profissional);
    Page<Paciente> visualizarTodos(Pageable pageable);
    void atualizar(PacienteCreateDto paciente, Integer id);
    void deletar(Integer id);
    Paciente ativar(Integer id_profissional);
    Paciente inativar(Integer id_profissional);
}
