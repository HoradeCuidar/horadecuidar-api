package com.hdc.hdc.prescricao_nutricional.modelo;

import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/modelos-nutricionais")
@PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
@RequiredArgsConstructor
public class ModeloNutricionalController {
    private final ModeloNutricionalService service;

    @GetMapping
    public List<ModeloNutricionalResumoDTO> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ModeloNutricionalResponseDTO buscar(@PathVariable Integer id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloNutricionalResponseDTO criar(@Valid @RequestBody ModeloNutricionalRequestDTO dto,
            @CurrentUser Usuario profissional) {
        return service.criar(dto, profissional.getId());
    }

    @PutMapping("/{id}")
    public ModeloNutricionalResponseDTO atualizar(@PathVariable Integer id,
            @Valid @RequestBody ModeloNutricionalRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @PostMapping("/{id}/duplicar")
    @ResponseStatus(HttpStatus.CREATED)
    public ModeloNutricionalResponseDTO duplicar(@PathVariable Integer id, @CurrentUser Usuario profissional) {
        return service.duplicar(id, profissional.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) { service.excluir(id); }
}
