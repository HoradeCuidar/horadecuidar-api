package com.hdc.hdc.service.interfaces;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.dto.response.PacienteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPacienteService {
    PacienteResponseDto cadastrar(PacienteCreateDto paciente);
    PacienteResponseDto visualizarPorId(Long id);
    PacienteResponseDto visualizarPorEmail(String email);
    Page<PacienteResponseDto> visualizarTodos(Integer pagina, Integer limite);
    void atualizar(PacienteCreateDto paciente, Long id);
    void deletar(Long id);
    PacienteResponseDto alterarStatus(Long id);
}
