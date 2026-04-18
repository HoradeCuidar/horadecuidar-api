package com.hdc.hdc.controller;

import com.hdc.hdc.dto.MedicamentoDTO;
import com.hdc.hdc.mapper.MedicamentoMapper;
import com.hdc.hdc.service.MedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;

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
