package com.hdc.hdc.orientacao_exercicio;

import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoRequestDTO;
import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoResponseDTO;
import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
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
@RequestMapping("/api/exercicios-orientacao")
public class ExercicioOrientacaoController {

    private final ExercicioOrientacaoService service;

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ExercicioOrientacaoResponseDTO> criar(
            @Valid @ModelAttribute ExercicioOrientacaoRequestDTO dto) {

        ExercicioOrientacaoResponseDTO criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
    public ResponseEntity<List<ExercicioOrientacaoResponseDTO>> listar(
            @RequestParam(required = false) CategoriaExercicio categoria) {

        List<ExercicioOrientacaoResponseDTO> lista = service.listar(categoria);
        return ResponseEntity.ok(lista);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<ExercicioOrientacaoResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute ExercicioOrientacaoRequestDTO dto) {

        ExercicioOrientacaoResponseDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {

        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
