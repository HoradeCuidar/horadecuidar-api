package com.hdc.hdc.controller;

import com.hdc.hdc.dto.create.ProfissionalDaSaudeCreateDTO;
import com.hdc.hdc.dto.response.ProfissionalDaSaudeResponseDTO;
import com.hdc.hdc.mapper.ProfissionalDaSaudeMapper;
import com.hdc.hdc.service.interfaces.IProfissionalDaSaudeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profissional")
public class ProfissionalDaSaudeController {

    private final IProfissionalDaSaudeService profissionalDaSaudeService;
    private final ProfissionalDaSaudeMapper profissionalDaSaudeMapper;

    @Autowired
    public ProfissionalDaSaudeController(IProfissionalDaSaudeService profissionalDaSaudeService,
                                         ProfissionalDaSaudeMapper profissionalDaSaudeMapper){
        this.profissionalDaSaudeService = profissionalDaSaudeService;
        this.profissionalDaSaudeMapper = profissionalDaSaudeMapper;
    }

    @PostMapping("/cadastrar")
    public ProfissionalDaSaudeResponseDTO cadastrar(
            @RequestBody @Valid ProfissionalDaSaudeCreateDTO profissionalDaSaudeCreateDTO
            ){
        return profissionalDaSaudeMapper.modeltoResponseDTO(
                    profissionalDaSaudeService.cadastrar(
                        profissionalDaSaudeMapper.createDTOtoModel(profissionalDaSaudeCreateDTO)));
    }
}