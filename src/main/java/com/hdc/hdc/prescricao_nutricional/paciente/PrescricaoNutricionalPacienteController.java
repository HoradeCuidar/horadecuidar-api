package com.hdc.hdc.prescricao_nutricional.paciente;

import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalRequestDTO;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nutricional")
public class PrescricaoNutricionalPacienteController {

    private final PrescricaoNutricionalPacienteService service;

    @PostMapping("/registrarAdesao/{id_paciente}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroAdesaoNutricionalResponseDTO> registrarAdesao(
            @PathVariable("id_paciente") Integer pacienteId,
            @Valid @RequestBody RegistroAdesaoNutricionalRequestDTO request) {

        RegistroAdesaoNutricionalResponseDTO resposta = service.registrarAdesao(pacienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/editarAdesao/{id_paciente}/{id_ocorrencia}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroAdesaoNutricionalResponseDTO> alterarAdesao(
            @PathVariable("id_paciente") Integer pacienteId,
            @PathVariable("id_ocorrencia") Integer ocorrenciaId,
            @Valid @RequestBody RegistroAdesaoNutricionalRequestDTO request) {

        RegistroAdesaoNutricionalResponseDTO resposta = service.alterarAdesao(pacienteId, ocorrenciaId, request);
        return ResponseEntity.ok(resposta);
    }
}
