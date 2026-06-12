package com.hdc.hdc.util.upload_fotos;

import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadConstructor {

    private final UploadService uploadService;

    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFISSIONAL_DA_SAUDE', 'ADMIN')")
    @PostMapping("/foto-perfil")
    public String uploadFotoMeuPerfil(
            @RequestParam("file") MultipartFile file,
            @CurrentUser Usuario usuarioAutenticado) throws IOException {
        return uploadService.uploadFotoMeuPerfil(file, usuarioAutenticado);
    }

    @PreAuthorize("hasAnyRole('PROFISSIONAL_DA_SAUDE', 'ADMIN')")
    @PostMapping("/foto-perfil/{usuarioId}")
    public String uploadFotoDePerfil(
            @PathVariable Integer usuarioId,
            @RequestParam("file") MultipartFile file,
            @CurrentUser Usuario usuarioAutenticado) throws IOException {
        return uploadService.uploadFotoDePerfil(file, usuarioId, usuarioAutenticado);
    }
}
