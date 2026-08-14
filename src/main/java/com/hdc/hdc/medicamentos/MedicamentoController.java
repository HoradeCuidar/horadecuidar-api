package com.hdc.hdc.medicamentos;

import com.hdc.hdc.medicamentos.dto.MedicamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamento")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final MedicamentoMapper medicamentoMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public MedicamentoDTO buscarOuCriar(@RequestParam String nome) {
        return medicamentoMapper.toDTO(medicamentoService.buscarOuCriar(nome));
    }

    @GetMapping("/buscar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public List<MedicamentoDTO> buscar(@RequestParam String nome) {
        return medicamentoMapper.toDTOList(medicamentoService.buscarSugestoes(nome));
    }
}
