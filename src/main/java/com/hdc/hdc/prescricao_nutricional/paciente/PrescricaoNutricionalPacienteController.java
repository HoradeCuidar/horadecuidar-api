package com.hdc.hdc.prescricao_nutricional.paciente;

import com.hdc.hdc.prescricao_nutricional.paciente.dto.RefeicaoDiaDTO;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalRequestDTO;
import com.hdc.hdc.prescricao_nutricional.paciente.dto.RegistroAdesaoNutricionalResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nutricional")
public class PrescricaoNutricionalPacienteController {

    private final PrescricaoNutricionalPacienteService service;

    @GetMapping("/visualizarRefeicoesDoDia")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public ResponseEntity<RefeicaoDiaDTO> listarRefeicoesDoDia(
            @CurrentUser Usuario usuario) {

        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());
        RefeicaoDiaDTO resposta = service.listarRefeicoesDoDia(usuario.getId(), hoje);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/registrarAdesao")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroAdesaoNutricionalResponseDTO> registrarAdesao(
            @CurrentUser Usuario usuario,
            @Valid @RequestBody RegistroAdesaoNutricionalRequestDTO request) {

        RegistroAdesaoNutricionalResponseDTO resposta = service.registrarAdesao(usuario.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/editarAdesao/{id_ocorrencia}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<RegistroAdesaoNutricionalResponseDTO> alterarAdesao(
            @CurrentUser Usuario usuario,
            @PathVariable("id_ocorrencia") Integer ocorrenciaId,
            @Valid @RequestBody RegistroAdesaoNutricionalRequestDTO request) {

        RegistroAdesaoNutricionalResponseDTO resposta = service.alterarAdesao(usuario.getId(), ocorrenciaId, request);
        return ResponseEntity.ok(resposta);
    }
}
