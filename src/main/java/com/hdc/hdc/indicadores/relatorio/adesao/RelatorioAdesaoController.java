package com.hdc.hdc.indicadores.relatorio.adesao;

import com.hdc.hdc.indicadores.relatorio.adesao.dto.DetalhamentoAdesaoMedicamentoResponseDTO;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.DetalhamentoAdesaoNutricionalResponseDTO;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.ResumoMedicamentoDTO;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.ResumoNutricionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{pacienteId}/relatorios/adesao")
public class RelatorioAdesaoController {
    
    private final RelatorioAdesaoMedicamentoService medicamentoService;
    private final RelatorioAdesaoNutricionalService nutricionalService;

    // Relatório resumido de adesão de medicação do paciente
    @GetMapping("/medicamentos/resumo")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ResumoMedicamentoDTO> resumoMedicamento(
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
    @GetMapping("/medicamentos/evolucao")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Object> evolucaoMedicamento(
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
    @GetMapping("/medicamentos/detalhamento")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<DetalhamentoAdesaoMedicamentoResponseDTO> detalhamentoMedicamento(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "limite", defaultValue = "10") Integer tamanho
    ) {
        return ResponseEntity.ok(medicamentoService.obterDetalhamento(pacienteId, dataInicial, dataFinal, pagina, tamanho));
    }

    // Relatório resumido de adesão nutricional do paciente
    @GetMapping("/nutricional/resumo")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ResumoNutricionalDTO> resumoNutricional(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
    ) {
        return ResponseEntity.ok(
                nutricionalService.obterResumo(
                        pacienteId,
                        dataInicial,
                        dataFinal
                )
        );
    }

    // Relatório de adesão semanal do paciente.
    @GetMapping("/nutricional/evolucao")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Object> evolucaoNutricional(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal
            ) {
        return ResponseEntity.ok(
                nutricionalService.obterEvolucaoSemanal(
                        pacienteId,
                        dataInicial,
                        dataFinal
                )
        );
    }

    /*
     * Esse endpoint alimentará a tabela detalhada e deverá ser paginado.
     * Cada linha representa as refeições previstas em determinada data.
    */
    @GetMapping("/nutricional/detalhamento")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<DetalhamentoAdesaoNutricionalResponseDTO> detalhamentoNutricional(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "limite", defaultValue = "10") Integer tamanho
    ) {
        return ResponseEntity.ok(nutricionalService.obterDetalhamento(pacienteId, dataInicial, dataFinal, pagina, tamanho));
    }
}
