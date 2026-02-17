package com.hdc.hdc.infra.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailMontagemService {

    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;

    // Monta e envia o e-mail de confirmação de cadastro de profissional.
    public void enviarConfirmacaoCadastro(String nome, String email, String username, String senha) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("nome", nome);
        variaveis.put("username", username);
        variaveis.put("senha", senha);

        enviarGeral(email, "Bem-vindo ao Hora de Cuidar", "email/cadastro", variaveis);
    }

    // Metodo generico para orquestrar o processamento do template e o envio.
    private void enviarGeral(String destinatario, String assunto, String template, Map<String, Object> variaveis) {
        // Processa o template HTML com as variáveis
        String html = emailTemplateService.processar(template, variaveis);

        // Define os recursos inline (imagens) comuns a todos os emails
        // Se necessário, isso pode ser passado como parâmetro tambem
        Map<String, String> recursosInline = new HashMap<>();
        recursosInline.put("logoImage", "static/logo-hdc.png");

        // Delega o envio para o serviço de baixo nível
        emailService.enviarHtml(destinatario, assunto, html, recursosInline);
    }
}
