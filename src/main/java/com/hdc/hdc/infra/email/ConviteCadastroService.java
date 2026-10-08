package com.hdc.hdc.infra.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConviteCadastroService {

    private final EmailMontagemService emailMontagemService;

    public boolean enviar(String nome, String email, String username, String senha) {
        try {
            emailMontagemService.enviarConfirmacaoCadastro(nome, email, username, senha);
            return true;
        } catch (RuntimeException e) {
            log.error("Cadastro salvo, mas o convite não foi aceito pelo serviço de e-mail.", e);
            return false;
        }
    }
}
