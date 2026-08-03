package com.hdc.hdc.prescricao_medicamentos.profissional;

import com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/medicamentos")
public class PrescricaoMedicamentoProfissionalController {

    private final PrescricaoMedicamentoProfissionalService prescricaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> criarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto,
            @CurrentUser Usuario profissional) {

        PrescricaoMedicamentoResponseDTO criada = prescricaoService.criarPrescricao(pacienteId, dto, profissional);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> atualizarPrescricao(
            @PathVariable("prescricaoId") UUID prescricaoId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto,
            @CurrentUser Usuario profissional) {

        PrescricaoMedicamentoResponseDTO atualizada = prescricaoService.atualizarPrescricao(prescricaoId, dto, profissional);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Void> deletarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        prescricaoService.deletarPrescricao(pacienteId, prescricaoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> alterarStatus(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        PrescricaoMedicamentoResponseDTO atualizada = prescricaoService.alterarStatus(pacienteId, prescricaoId);
        return ResponseEntity.ok(atualizada);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<List<PrescricaoMedicamentoResponseDTO>> listarPrescricoesAtivas(@PathVariable("id") Integer pacienteId) {
        List<PrescricaoMedicamentoResponseDTO> ativas = prescricaoService.listarPrescricoesAtivas(pacienteId);
        return ResponseEntity.ok(ativas);
    }

    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<List<PrescricaoMedicamentoResponseDTO>> listarHistorico(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoMedicamentoResponseDTO> historico = prescricaoService.listarHistorico(pacienteId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{prescricaoId}/relatorio")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<RelatorioAdesaoDTO> gerarRelatorioAdesao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        RelatorioAdesaoDTO relatorio = prescricaoService.gerarRelatorioAdesao(pacienteId, prescricaoId);
        return ResponseEntity.ok(relatorio);
    }
}
