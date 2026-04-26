package com.hdc.hdc.util.construct;

import com.hdc.hdc.auth.autenticacao.dto.UsuarioRegisterDTO;
import com.hdc.hdc.infra.security.service.UsuarioService;
import com.hdc.hdc.usuarios.enums.Role;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateAdmin {

    private final String ADMIN_EMAIL;
    private final String ADMIN_USERNAME;
    private final String ADMIN_PASSWORD;
    private final UsuarioService usuarioService;

    public CreateAdmin(
            @Value("${ADMIN_USERNAME:admin-dev}") String root,
            @Value("${ADMIN_PASSWORD:admin123}") String passwordForRoot,
            @Value("${ADMIN_EMAIL:admin.dev@example.com}") String rootEmail,
            UsuarioService usuarioService) {
        this.ADMIN_USERNAME = root;
        this.ADMIN_PASSWORD = passwordForRoot;
        this.ADMIN_EMAIL = rootEmail;
        this.usuarioService = usuarioService;
    }

    @PostConstruct
    public void init() {
        if (usuarioService.existsByEmail(ADMIN_EMAIL)) {
            log.info("Usuário administrador já existe [Skipping creation].");
            return;
        }

        log.warn("Creating admin user: {}", ADMIN_USERNAME);
        UsuarioRegisterDTO usuario = new UsuarioRegisterDTO(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
        
        try {
            usuarioService.save(usuario);
            log.info("Usuário administrador criado com sucesso.");
        } catch (Exception e) {
            log.error("Falha ao cria o usuário administrador: {}", e.getMessage());
        }
    }
}
