package com.hdc.hdc.pacientes;

import com.hdc.hdc.pacientes.dto.PacienteCreateDto;
import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.pacientes.dto.PacienteSelfUpdateDto;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PACIENTE')")
    public ResponseEntity<PacienteResponseDto> perfil(@CurrentUser Usuario usuario) {
        return ResponseEntity.ok(pacienteService.visualizarPerfil(usuario.getId()));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PacienteResponseDto> cadastrar(@Valid @RequestBody PacienteCreateDto paciente) {
        var created = pacienteService.cadastrar(paciente);
        URI uri = URI.create("/paciente/" + created.id());

        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PacienteResponseDto> visualizarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pacienteService.visualizarPorId(id));
    }

    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PacienteResponseDto> visualizarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(pacienteService.visualizarPorEmail(email));
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Page<PacienteResponseDto>> visualizarTodos(
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "limite", defaultValue = "10") Integer limite
    ) {
        return ResponseEntity.ok(pacienteService.visualizarTodos(pagina, limite));
    }

    @GetMapping("/nome")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<Page<PacienteResponseDto>> visualizarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(pacienteService.encontrarPorNome(nome));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public void atualizar(@RequestBody PacienteCreateDto paciente, @PathVariable Integer id) {
        this.pacienteService.atualizar(paciente, id);
    }

    @PutMapping("/perfil")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<PacienteResponseDto> atualizarPerfil(
            @Valid @RequestBody PacienteSelfUpdateDto dto,
            @CurrentUser Usuario usuarioLogado
    ) {
        PacienteResponseDto updated = pacienteService.atualizarPerfil(dto, usuarioLogado.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public void deletar(@PathVariable Integer id) {
        this.pacienteService.deletar(id);
    }

    @PatchMapping("/status/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
    public ResponseEntity<PacienteResponseDto> alterarStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(this.pacienteService.alterarStatus(id));
    }
}
