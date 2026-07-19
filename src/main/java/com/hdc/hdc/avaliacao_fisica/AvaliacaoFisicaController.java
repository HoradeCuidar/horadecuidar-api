package com.hdc.hdc.avaliacao_fisica;

import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaRequestDTO;
import com.hdc.hdc.avaliacao_fisica.dto.AvaliacaoFisicaResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pacientes/{id}/avaliacoes-fisicas")
public class AvaliacaoFisicaController {

    private final AvaliacaoFisicaService avaliacaoFisicaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<AvaliacaoFisicaResponseDTO> criarAvaliacao(
            @PathVariable("id") Integer pacienteId,
            @Valid @RequestBody AvaliacaoFisicaRequestDTO dto,
            @CurrentUser Usuario profissional) {

        AvaliacaoFisicaResponseDTO criada = avaliacaoFisicaService.registrarAvaliacao(pacienteId, dto, profissional);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public ResponseEntity<Object> listarAvaliacoes(
            @PathVariable("id") Integer pacienteId,
            @CurrentUser Usuario usuario) {

        Object avaliacoes = avaliacaoFisicaService.listarAvaliacoes(pacienteId, usuario);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{avaliacaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public ResponseEntity<AvaliacaoFisicaResponseDTO> buscarPorId(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("avaliacaoId") Long avaliacaoId) {

        AvaliacaoFisicaResponseDTO avaliacao = avaliacaoFisicaService.buscarPorId(pacienteId, avaliacaoId);
        return ResponseEntity.ok(avaliacao);
    }

    @PutMapping("/{avaliacaoId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<AvaliacaoFisicaResponseDTO> atualizarAvaliacao(
            @PathVariable("id") Integer pacienteId,
            @PathVariable("avaliacaoId") Long avaliacaoId,
            @Valid @RequestBody AvaliacaoFisicaRequestDTO dto,
            @CurrentUser Usuario profissional) {

        AvaliacaoFisicaResponseDTO atualizada = avaliacaoFisicaService.atualizarAvaliacao(pacienteId, avaliacaoId, dto, profissional);
        return ResponseEntity.ok(atualizada);
    }
}
