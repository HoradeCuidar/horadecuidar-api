package com.hdc.hdc.service;

import com.hdc.hdc.dto.create.ResetPasswordCreateDto;
import com.hdc.hdc.dto.create.SolicitarRecuperacaoDto;
import com.hdc.hdc.infra.email.EmailMontagemService;
import com.hdc.hdc.model.TokenRecuperacao;
import com.hdc.hdc.model.Usuario;
import com.hdc.hdc.repository.TokenRepository;
import com.hdc.hdc.repository.UsuarioRepository;
import com.hdc.hdc.util.exception.InvalidTokenException;
import com.hdc.hdc.util.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;
    private final EmailMontagemService emailMontagemService;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void solicitarRecuperacaoSenha(SolicitarRecuperacaoDto dto) {
        try {
            usuarioRepository.findByEmail(dto.email()).ifPresent(usuario -> {

                // invalidar todos os tokens antigos
                tokenRepository.invalidateAllByusuario(usuario);

                TokenRecuperacao token = new TokenRecuperacao();
                token.setToken(UUID.randomUUID().toString());
                token.setUsuario(usuario);
                token.setExpiracao(LocalDateTime.now().plusMinutes(30));
                token.setUsado(false);

                tokenRepository.save(token);

                String link = frontendUrl +
                        "/reset-password?token=" + token.getToken();

                this.emailMontagemService.enviarRecuperacaoSenha(
                        usuario.getNome(),
                        usuario.getEmail(),
                        30,
                        link
                );
                log.info("Email de recuperacao de senha enviado para {}", dto.email());
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public void resetarSenha(ResetPasswordCreateDto dto) {
        if(!dto.novaSenha().equals(dto.confirmacao())) {
            throw new InvalidValueException("Senha", "A confirmação da senha deve ser igual a senha informada.");
        }

        TokenRecuperacao token = tokenRepository
                .findByToken(dto.token())
                .orElseThrow(InvalidTokenException::new);

        if (token.isUsado() || token.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }

        Usuario user = token.getUsuario();

        user.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(user);

        token.setUsado(true);
        tokenRepository.save(token);

        this.emailMontagemService.enviarResetSenha(
                user.getEmail(),
                user.getNome(),
                LocalDateTime.now(),
                "localhost:5173/login"
        );
        log.info("Reset de senha realizado para: {}", user.getUsername());
    }
}
