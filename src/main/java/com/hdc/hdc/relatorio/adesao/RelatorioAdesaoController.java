package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.relatorio.adesao.dto.DetalhamentoAdesaoMedicamentoResponseDTO;
import com.hdc.hdc.relatorio.adesao.dto.ResumoMedicamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{pacienteId}/relatorios/adesao/medicamentos")
public class RelatorioAdesaoController {
    
    private final RelatorioAdesaoMedicamentoService medicamentoService;

    // Relatório resumido de adesão de medicação do paciente
    @GetMapping("/resumo")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ResumoMedicamentoDTO> resumo(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return ResponseEntity.ok(
                medicamentoService.obterResumo(
                        pacienteId,
                        dataInicial,
                        dataFinal
                )
        );
    }

    // Relatório de adesão semanal do paciente.
    @GetMapping("/evolucao")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Object> evolucao(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
            ) {
        return ResponseEntity.ok(
                medicamentoService.obterEvolucaoSemanal(
                        pacienteId,
                        dataInicial,
                        dataFinal
                )
        );
    }

    /*
     * Esse endpoint alimentará a tabela detalhada e deverá ser paginado.
     * Cada linha pode representar um medicamento em determinada data:
    */
    @GetMapping("/detalhamento")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<DetalhamentoAdesaoMedicamentoResponseDTO> detalhamento(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "limite", defaultValue = "10") Integer tamanho
    ) {
        return ResponseEntity.ok(medicamentoService.obterDetalhamento(pacienteId, dataInicial, dataFinal, pagina, tamanho));
    }
}
