package com.hdc.hdc.profissionais_saude;

import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeCreateDTO;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeResponseDTO;
import com.hdc.hdc.profissionais_saude.dto.ProfissionalDaSaudeUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/profissional")
public class ProfissionalDaSaudeController {

    private final ProfissionalDaSaudeService profissionalDaSaudeService;
    private final ProfissionalDaSaudeMapper profissionalDaSaudeMapper;

    @Autowired
    public ProfissionalDaSaudeController(ProfissionalDaSaudeService profissionalDaSaudeService,
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

    @GetMapping("/visualizar/{id_profissional}")
    public ProfissionalDaSaudeResponseDTO visualizar(
            @PathVariable Integer id_profissional
    ){
        return profissionalDaSaudeMapper.modeltoResponseDTO(profissionalDaSaudeService.visualizar(id_profissional));
    }

    @GetMapping("/visualizarTodos")
    public Page<ProfissionalDaSaudeResponseDTO> visualizarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<ProfissionalDaSaude> profissionalDaSaudePage = profissionalDaSaudeService.visualizarTodos(pageable);
        List<ProfissionalDaSaudeResponseDTO> profissionalDaSaudeResponseDTOList = profissionalDaSaudeMapper.modeltoResponseDTO(profissionalDaSaudePage.getContent());
        return new PageImpl<>(profissionalDaSaudeResponseDTOList, pageable, profissionalDaSaudePage.getTotalElements());
    }

    @PutMapping("/editar/{id_profissional}")
    public ProfissionalDaSaudeResponseDTO cadastrar(
            @RequestBody @Valid ProfissionalDaSaudeUpdateDTO profissionalDaSaudeUpdateDTO,
            @PathVariable Integer id_profissional
    ){
        return profissionalDaSaudeMapper.modeltoResponseDTO(
                profissionalDaSaudeService.editar(
                        profissionalDaSaudeMapper.updateDTOtoModel(profissionalDaSaudeUpdateDTO), id_profissional));
    }

    @PutMapping("/ativar/{id_profissional}")
    public ProfissionalDaSaudeResponseDTO ativar(
            @PathVariable Integer id_profissional
    ){
        return profissionalDaSaudeMapper.modeltoResponseDTO(profissionalDaSaudeService.ativar(id_profissional));
    }

    @PutMapping("/inativar/{id_profissional}")
    public ProfissionalDaSaudeResponseDTO inativar(
            @PathVariable Integer id_profissional
    ){
        return profissionalDaSaudeMapper.modeltoResponseDTO(profissionalDaSaudeService.inativar(id_profissional));
    }

    @GetMapping("/buscar")
    public Page<ProfissionalDaSaudeResponseDTO> buscar(
            @RequestParam String nome,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<ProfissionalDaSaude> profissionalDaSaudePage = profissionalDaSaudeService.buscar(nome, pageable);
        List<ProfissionalDaSaudeResponseDTO> profissionalDaSaudeResponseDTOList = profissionalDaSaudeMapper.modeltoResponseDTO(profissionalDaSaudePage.getContent());
        return new PageImpl<>(profissionalDaSaudeResponseDTOList, pageable, profissionalDaSaudePage.getTotalElements());
    }

    @PostMapping("/uploadFotoDePerfil/{id_profissional}")
    public String upload(
            @PathVariable Integer id_profissional,
            @RequestParam("file") MultipartFile file) throws IOException {
        return profissionalDaSaudeService.uploadFotoDePerfil(id_profissional, file);
    }
}