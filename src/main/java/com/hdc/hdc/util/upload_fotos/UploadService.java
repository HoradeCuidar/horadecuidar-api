package com.hdc.hdc.util.upload_fotos;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.UsuarioRepository;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import com.hdc.hdc.util.exception.model.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final UsuarioRepository usuarioRepository;
    private final R2Service r2Service;

    public String uploadFotoMeuPerfil(MultipartFile fotoDePerfil, Usuario currentUser) throws IOException {
        validarArquivo(fotoDePerfil);

        Usuario user = usuarioRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ID do Usuário", "Usuário não encontrado com o id informado."));

        return this.inserirFotoPerfil(fotoDePerfil, user);
    }

    public String uploadFotoDePerfil(MultipartFile fotoDePerfil, Integer usuarioId, Usuario currentUser) throws IOException {
        validarArquivo(fotoDePerfil);

        Usuario user = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("ID do Usuário", "Usuário não encontrado com o id informado."));

        validarPermissaoEdicao(currentUser, user);

        return this.inserirFotoPerfil(fotoDePerfil, user);
    }

    private String inserirFotoPerfil(MultipartFile fotoDePerfil, Usuario user) throws IOException {
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

    private void validarPermissaoEdicao(Usuario currentUser, Usuario targetUser) {
        if (currentUser.getId().equals(targetUser.getId())) {
            return;
        }

        Role currentRole = currentUser.getRole();
        Role targetRole = targetUser.getRole();

        if (currentRole == Role.ADMIN) {
            return;
        }

        if (currentRole == Role.PACIENTE) {
            if (targetRole == Role.PROFISSIONAL_DA_SAUDE || targetRole == Role.ADMIN) {
                throw new InvalidOperationException("usuarioId", "Paciente não tem permissão para editar profissional de saúde ou administrador.");
            }
            throw new InvalidOperationException("usuarioId", "Paciente não tem permissão para editar outro usuário.");
        }

        if (currentRole == Role.PROFISSIONAL_DA_SAUDE
                && (targetRole == Role.ADMIN || targetRole == Role.PROFISSIONAL_DA_SAUDE)) {
            throw new InvalidOperationException("usuarioId", "Profissional de saúde não tem permissão para editar este usuário.");
        }
    }

    private void validarArquivo(MultipartFile fotoDePerfil) {
        if (fotoDePerfil == null || fotoDePerfil.isEmpty()) {
            throw new InvalidValueException("file", "O arquivo da foto de perfil é obrigatório.");
        }
    }
}
