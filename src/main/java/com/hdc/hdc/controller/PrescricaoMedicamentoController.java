package com.hdc.hdc.controller;

import com.hdc.hdc.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.model.ProfissionalDaSaude;
import com.hdc.hdc.service.PrescricaoMedicamentoService;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/prescricoes/medicamentos")
public class PrescricaoMedicamentoController {

    private final PrescricaoMedicamentoService prescricaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> criarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto,
            @CurrentUser ProfissionalDaSaude profissional) {

        PrescricaoMedicamentoResponseDTO criada = prescricaoService.criarPrescricao(pacienteId, dto, profissional);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{prescricaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> atualizarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto,
            @CurrentUser ProfissionalDaSaude profissional) {

        PrescricaoMedicamentoResponseDTO atualizada = prescricaoService.atualizarPrescricao(pacienteId, prescricaoId, dto, profissional);
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
