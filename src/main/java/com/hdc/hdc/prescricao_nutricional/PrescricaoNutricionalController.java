package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalCreateDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResponseDTO;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutricional")
public class PrescricaoNutricionalController {

    private final PrescricaoNutricionalService prescricaoNutricionalService;
    private final PrescricaoNutricionalMapper prescricaoNutricionalMapper;

    @Autowired
    PrescricaoNutricionalController(PrescricaoNutricionalService prescricaoNutricionalService,
                                    PrescricaoNutricionalMapper prescricaoNutricionalMapper){
        this.prescricaoNutricionalService = prescricaoNutricionalService;
        this.prescricaoNutricionalMapper = prescricaoNutricionalMapper;
    }

    @PostMapping("cadastrar")
    @PreAuthorize("hasAnyRole('PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoNutricionalResponseDTO> cadastrar(
            @Valid @RequestBody PrescricaoNutricionalCreateDTO prescricaoNutricionalCreateDTO,
            @CurrentUser Usuario profissionalDaSaude
            ) {

        prescricaoNutricionalCreateDTO.setProfissionalId(profissionalDaSaude.getId());

        PrescricaoNutricionalResponseDTO response = prescricaoNutricionalService.cadastrar(
                prescricaoNutricionalMapper.createDTOtoModel(prescricaoNutricionalCreateDTO)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("visualizar/{id_prescricao}")
    @PreAuthorize("hasAnyRole('PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PrescricaoNutricionalResponseDTO> visualizar(@PathVariable Integer id_prescricao) {

        PrescricaoNutricionalResponseDTO response = prescricaoNutricionalService.visualizar(id_prescricao);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}