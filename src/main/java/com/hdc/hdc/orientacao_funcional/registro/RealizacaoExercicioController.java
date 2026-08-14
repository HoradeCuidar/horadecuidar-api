package com.hdc.hdc.orientacao_funcional.registro;

import com.hdc.hdc.orientacao_funcional.registro.dto.RegistroRealizacaoFuncionalRequestDTO;
import com.hdc.hdc.orientacao_funcional.registro.dto.RegistroRealizacaoFuncionalResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paciente/{pacienteId}/realizacao-funcional")
public class RealizacaoExercicioController {

    private final RealizacaoExercicioService realizacaoExercicioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroRealizacaoFuncionalResponseDTO> registrarRealizacaoFuncional(
            @PathVariable("pacienteId") Integer pacienteId,
            @Valid @RequestBody RegistroRealizacaoFuncionalRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(realizacaoExercicioService.registrarRealizacao(pacienteId, request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroRealizacaoFuncionalResponseDTO> alterarRealizacaoFuncional(
            @PathVariable("pacienteId") Integer pacienteId,
            @PathVariable("id") Long registroId,
            @Valid @RequestBody RegistroRealizacaoFuncionalRequestDTO request){
        RegistroRealizacaoFuncionalResponseDTO response = realizacaoExercicioService.alterarRealizacao(pacienteId, registroId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroRealizacaoFuncionalResponseDTO> alterarStatus(
            @PathVariable("pacienteId") Integer pacienteId,
            @PathVariable("id") Long exercicioId,
            @RequestParam StatusRealizacao novoStatus){
        return ResponseEntity.ok(realizacaoExercicioService.alterarStatus(pacienteId, exercicioId, novoStatus));
    }

    @PatchMapping("/{id}/sensacao-final")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroRealizacaoFuncionalResponseDTO> alterarSensacaoFinal(
            @PathVariable("pacienteId") Integer pacienteId,
            @PathVariable("id") Long exercicioId,
            @RequestParam SensacaoFinal sensacaoFinal){
        return ResponseEntity.ok(realizacaoExercicioService.alterarSensacaoFinal(pacienteId, exercicioId, sensacaoFinal));
    }

    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<Page<RegistroRealizacaoFuncionalResponseDTO>> historicoDoPaciente(
            @PathVariable Integer pacienteId,
            @RequestParam(value = "number-page", defaultValue = "0") Integer numberPage,
            @RequestParam(value = "page-size", defaultValue = "15") Integer pageSize
    ) {
        return ResponseEntity.ok(realizacaoExercicioService.historicoDoPaciente(pacienteId, numberPage, pageSize));
    }
}
