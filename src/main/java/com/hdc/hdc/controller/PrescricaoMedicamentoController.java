package com.hdc.hdc.controller;

import com.hdc.hdc.dto.PrescricaoMedicamentoRequestDTO;
import com.hdc.hdc.dto.PrescricaoMedicamentoResponseDTO;
import com.hdc.hdc.dto.RelatorioAdesaoDTO;
import com.hdc.hdc.service.PrescricaoMedicamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes/{id}/prescricoes/medicamentos")
public class PrescricaoMedicamentoController {

    @Autowired
    private PrescricaoMedicamentoService prescricaoService;

    @PostMapping
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> criarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto) {

        PrescricaoMedicamentoResponseDTO criada = prescricaoService.criarPrescricao(pacienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{prescricaoId}")
    public ResponseEntity<PrescricaoMedicamentoResponseDTO> atualizarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId,
            @Valid @RequestBody PrescricaoMedicamentoRequestDTO dto) {

        PrescricaoMedicamentoResponseDTO atualizada = prescricaoService.atualizarPrescricao(pacienteId, prescricaoId,
                dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{prescricaoId}")
    public ResponseEntity<Void> deletarPrescricao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        prescricaoService.deletarPrescricao(pacienteId, prescricaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PrescricaoMedicamentoResponseDTO>> listarPrescricoesAtivas(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoMedicamentoResponseDTO> ativas = prescricaoService.listarPrescricoesAtivas(pacienteId);
        return ResponseEntity.ok(ativas);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PrescricaoMedicamentoResponseDTO>> listarHistorico(
            @PathVariable("id") Integer pacienteId) {

        List<PrescricaoMedicamentoResponseDTO> historico = prescricaoService.listarHistorico(pacienteId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/{prescricaoId}/relatorio")
    public ResponseEntity<RelatorioAdesaoDTO> gerarRelatorioAdesao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("prescricaoId") UUID prescricaoId) {

        RelatorioAdesaoDTO relatorio = prescricaoService.gerarRelatorioAdesao(pacienteId, prescricaoId);
        return ResponseEntity.ok(relatorio);
    }
}
