package com.hdc.hdc.prescricao_exercicios.profissional;

import com.hdc.hdc.prescricao_exercicios.dto.PrescricaoExercicioRequestDTO;
import com.hdc.hdc.prescricao_exercicios.dto.PrescricaoExercicioResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de prescrições de exercícios acessíveis por profissionais de saúde e administradores.
 *
 * <p>Base URL: {@code /api/pacientes/{id}/prescricoes/exercicios}
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/exercicios")
public class PrescricaoExercicioProfissionalController {

    private final PrescricaoExercicioProfissionalService prescricaoService;

    /** Cria uma nova prescrição de exercícios para o paciente. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoExercicioResponseDTO> criarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody PrescricaoExercicioRequestDTO dto,
            @CurrentUser Usuario profissional) {

        PrescricaoExercicioResponseDTO criada = prescricaoService.criarPrescricao(pacienteId, dto, profissional);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    /** Substitui integralmente o conteúdo de uma prescrição existente. */
    @PutMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoExercicioResponseDTO> atualizarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId,
            @Valid @RequestBody PrescricaoExercicioRequestDTO dto,
            @CurrentUser Usuario profissional) {

        PrescricaoExercicioResponseDTO atualizada = prescricaoService.atualizarPrescricao(pacienteId, prescricaoId, dto, profissional);
        return ResponseEntity.ok(atualizada);
    }

    /** Remove permanentemente uma prescrição (hard delete). */
    @DeleteMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Void> deletarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        prescricaoService.deletarPrescricao(pacienteId, prescricaoId);
        return ResponseEntity.noContent().build();
    }

    /** Alterna o status ativo/inativo da prescrição (soft deactivation). */
    @PatchMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoExercicioResponseDTO> alterarStatus(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        PrescricaoExercicioResponseDTO atualizada = prescricaoService.alterarStatus(pacienteId, prescricaoId);
        return ResponseEntity.ok(atualizada);
    }

    /** Lista todas as prescrições de exercícios ativas do paciente. */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<List<PrescricaoExercicioResponseDTO>> listarPrescricoesAtivas(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoExercicioResponseDTO> ativas = prescricaoService.listarPrescricoesAtivas(pacienteId);
        return ResponseEntity.ok(ativas);
    }

    /** Retorna o histórico completo de prescrições (inativas e encerradas) do paciente. */
    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<List<PrescricaoExercicioResponseDTO>> listarHistorico(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoExercicioResponseDTO> historico = prescricaoService.listarHistorico(pacienteId);
        return ResponseEntity.ok(historico);
    }
}
