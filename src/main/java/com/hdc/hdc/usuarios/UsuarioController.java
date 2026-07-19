package com.hdc.hdc.usuarios;

import com.hdc.hdc.infra.security.service.UsuarioService;
import com.hdc.hdc.usuarios.dto.UsuarioDTO;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE', 'PACIENTE')")
    public ResponseEntity<UsuarioDTO> getUsuario(@CurrentUser Usuario usuario){
        return ResponseEntity.ok().body(usuarioService.buscarPorEmail(usuario.getEmail()));
    }
}
