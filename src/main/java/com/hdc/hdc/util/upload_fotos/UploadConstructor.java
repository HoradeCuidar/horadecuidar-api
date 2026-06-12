package com.hdc.hdc.util.upload_fotos;

import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.notations.currenteUser.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadConstructor {

    private final UploadService uploadService;

    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFISSIONAL_DA_SAUDE', 'ADMIN')")
    @PostMapping("/foto-perfil")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @CurrentUser Usuario usuarioAutenticado) throws IOException {
        return uploadService.uploadFotoDePerfil(file, usuarioAutenticado);
    }
}
