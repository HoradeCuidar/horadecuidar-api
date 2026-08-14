package com.hdc.hdc.auth.recuperacao_senha;

import com.hdc.hdc.auth.recuperacao_senha.dto.ResetPasswordCreateDto;
import com.hdc.hdc.auth.recuperacao_senha.dto.SolicitarRecuperacaoDto;
import com.hdc.hdc.infra.email.EmailMontagemService;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.UsuarioRepository;
import com.hdc.hdc.util.exception.InvalidTokenException;
import com.hdc.hdc.util.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${admin.address}")
    private String adminAddress;

    public void solicitarRecuperacaoSenha(SolicitarRecuperacaoDto dto) {
        usuarioRepository.findByEmail(dto.email()).ifPresent(usuario -> {

            if(usuario.getEmail().equals(adminAddress)) {
                log.info("Administrador não pode recuperar senha!");
                return;
            }

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
