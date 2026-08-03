package com.hdc.hdc.relatorio.orientacao;

import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentoOrientacaoFuncionalResponseDTO;
import com.hdc.hdc.relatorio.orientacao.dto.ResumoFuncionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{pacienteId}/relatorios/adesao/orientacoes-funcionais")
public class RelatorioOrientacaoFuncionalController {

    private final RelatorioOrientacaoFuncionalService relatorioFuncionalService;

    @GetMapping("/resumo")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ResumoFuncionalDTO> resumo(
            @PathVariable Integer pacienteId,
            @RequestParam(name = "data-inicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(name = "data-final") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal

    ) {
        return ResponseEntity.ok(
                relatorioFuncionalService.obterResumoFuncional(
                        pacienteId,
                        dataInicial,
                        dataFinal
                ));
    }

    @GetMapping("/detalhamento")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<DetalhamentoOrientacaoFuncionalResponseDTO> detalhamento(
            @PathVariable Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") Integer tamanho

    ) {
        return ResponseEntity.ok(
                relatorioFuncionalService.obterDetalhamentoFuncional(
                        pacienteId,
                        dataInicial,
                        dataFinal,
                        pagina,
                        tamanho
                ));
    }
}
