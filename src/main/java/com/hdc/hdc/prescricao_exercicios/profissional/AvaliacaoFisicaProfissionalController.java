package com.hdc.hdc.prescricao_exercicios.profissional;

import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.prescricao_exercicios.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/exercicios")
public class AvaliacaoFisicaProfissionalController {

    private final AvaliacaoFisicaProfissionalService prescricaoService;

    // Cria uma nova prescrição de exercícios para o paciente.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<AvaliacaoFisicaResponseDTO> criarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody AvaliacaoFisicaRequestDTO dto,
            @CurrentUser Usuario profissional) {

        AvaliacaoFisicaResponseDTO criada = prescricaoService.registrarAvaliacao(pacienteId, dto, profissional);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // Substitui integralmente o conteúdo de uma prescrição existente.
    @PutMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<AvaliacaoFisicaResponseDTO> atualizarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId,
            @Valid @RequestBody AvaliacaoFisicaRequestDTO dto,
            @CurrentUser Usuario profissional) {

        AvaliacaoFisicaResponseDTO atualizada = prescricaoService.atualizarAvaliacao(pacienteId, prescricaoId, dto, profissional);
        return ResponseEntity.ok(atualizada);
    }

    // Remove permanentemente uma prescrição (hard delete).
    @DeleteMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Void> deletarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        prescricaoService.deletarAvaliacao(pacienteId, prescricaoId);
        return ResponseEntity.noContent().build();
    }

    // Retorna o histórico completo de prescrições (inativas e encerradas) do paciente.
    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Page<AvaliacaoFisicaResponseDTO>> listarHistorico(
            @PathVariable("id") Integer pacienteId,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        Page<AvaliacaoFisicaResponseDTO> historico = prescricaoService.listarHistorico(pacienteId, pageNumber, pageSize);
        return ResponseEntity.ok(historico);
    }
}
