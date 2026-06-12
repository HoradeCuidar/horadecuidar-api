package com.hdc.hdc.prescricao_exercicios.paciente;

import com.hdc.hdc.prescricao_exercicios.paciente.dto.ExercicioDiaDTO;
import com.hdc.hdc.prescricao_exercicios.paciente.dto.RegistroRealizacaoRequestDTO;
import com.hdc.hdc.prescricao_exercicios.paciente.dto.RegistroRealizacaoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/exercicios/paciente")
public class PrescricaoExercicioPacienteController {

    private final PrescricaoExercicioPacienteService service;

    @GetMapping("/hoje")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<List<ExercicioDiaDTO>> listarExerciciosDoDia(
            @PathVariable("id") Integer pacienteId) {

        List<ExercicioDiaDTO> itens = service.listarExerciciosDoDia(pacienteId);
        return ResponseEntity.ok(itens);
    }

    @PostMapping("/realizacao")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'PACIENTE')")
    public ResponseEntity<RegistroRealizacaoResponseDTO> registrarRealizacao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody RegistroRealizacaoRequestDTO request) {

        RegistroRealizacaoResponseDTO resposta = service.registrarRealizacao(pacienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/realizacao/{realizacaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<RegistroRealizacaoResponseDTO> alterarRealizacao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("realizacaoId") Long realizacaoId,
            @Valid @RequestBody RegistroRealizacaoRequestDTO request) {

        RegistroRealizacaoResponseDTO resposta = service.alterarRealizacao(pacienteId, realizacaoId, request);
        return ResponseEntity.ok(resposta);
    }
}
