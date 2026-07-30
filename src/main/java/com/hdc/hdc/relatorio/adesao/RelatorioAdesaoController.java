package com.hdc.hdc.relatorio.adesao;

import com.hdc.hdc.relatorio.adesao.dto.DetalhamentoDiarioDTO;
import com.hdc.hdc.relatorio.adesao.dto.ResumoMedicamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    /*
    * consulta utilizada pelo gráfico.
    * Ela poderá agrupar por:
        dia;
        semana;
        mês.
    * */
    @GetMapping("/evolucao")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Object> evolucao(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @PathVariable("agrupamento") AgrupamentoRelatorio agrupamento
            ) {
        return null;
    }


    /*
     * Esse endpoint alimentará a tabela detalhada e deverá ser paginado.
     * Cada linha pode representar um medicamento em determinada data:
        {
          "conteudo": [
            {
              "data": "2026-07-10",
              "medicamento": "Losartana",
              "esperado": 2,
              "realizado": 1,
              "naoRealizado": 1,
              "semRegistro": 0,
              "percentualAdesao": 50.0
            }
          ],
          "pagina": 0,
          "totalPaginas": 2
        }
    */
    @GetMapping("/detalhamento")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Page<DetalhamentoDiarioDTO>> detalhamento(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @PathVariable("pagina") Integer pagina,
            @PathVariable("tamanho") Integer tamanho
    ) {
        return null;
    }
}
