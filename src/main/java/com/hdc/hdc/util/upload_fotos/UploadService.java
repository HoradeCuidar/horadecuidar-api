package com.hdc.hdc.util.upload_fotos;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.UsuarioRepository;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final UsuarioRepository usuarioRepository;
    private final R2Service r2Service;

    public String uploadFotoDePerfil(MultipartFile fotoDePerfil, Usuario currentUser) throws IOException {
        validarArquivo(fotoDePerfil);

        Usuario user = usuarioRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ID do Usuário", "Usuário não encontrado com o id informado."));

        String url = r2Service.upload(fotoDePerfil);
        String fotoAtual = user.getFotoDePerfil();

        if (fotoAtual == null || fotoAtual.isEmpty()) {
            user.setFotoDePerfil(url);
        } else {
            r2Service.delete(fotoAtual);
            user.setFotoDePerfil(url);
        }

        usuarioRepository.save(user);
        return url;
    }

    private void validarArquivo(MultipartFile fotoDePerfil) {
        if (fotoDePerfil == null || fotoDePerfil.isEmpty()) {
            throw new InvalidValueException("file", "O arquivo da foto de perfil é obrigatório.");
        }
    }
}
