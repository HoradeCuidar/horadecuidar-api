package com.hdc.hdc.infra.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailMontagemService {

    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${frontend.url}")
    private String frontendUrl;

    // E-mail de confirmacao de cadastro de profissional
    public void enviarConfirmacaoCadastro(String nome, String email, String username, String senha) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("nome", nome);
        variaveis.put("username", username);
        variaveis.put("senha", senha);

        this.enviarGeral(email, "Bem-vindo ao Hora de Cuidar", "email/cadastro", variaveis);
    }

    //E-mail de recuperacao de senha
    public void enviarRecuperacaoSenha(String nome, String email, Integer tempoExpiracao, String resetLink) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("nome", nome);
        variaveis.put("tempoExpiracao", tempoExpiracao);
        variaveis.put("resetLink", resetLink);

        this.enviarGeral(email, "Recuperação de Senha - Hora de Cuidar", "email/solicitacao_senha", variaveis);
    }

    // E-mail de confirmacao de recuperacao de senha
    public void enviarResetSenha(String email, String nome, LocalDateTime dataHoraAlteracao) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("nome", nome);
        variaveis.put("dataHoraAlteracao", dataHoraAlteracao.format(formatter));

        this.enviarGeral(email, "Informe de senha alterada", "email/senha_recuperada", variaveis);
    }

    // Metodo generico para orquestrar o processamento do template e o envio.
    private void enviarGeral(String destinatario, String assunto, String template, Map<String, Object> variaveis) {
        variaveis.putIfAbsent("loginUrl", montarUrlFrontend("/login"));

        // Processa o template HTML com as variáveis
        String html = emailTemplateService.processar(template, variaveis);

        // Define os recursos inline comuns a todos os emails
        Map<String, String> recursosInline = new HashMap<>();
        recursosInline.put("logoImage", "static/logo-hdc.png");

        emailService.enviarHtml(destinatario, assunto, html, recursosInline);
    }

    private String montarUrlFrontend(String caminho) {
        return frontendUrl.replaceAll("/+$", "") + caminho;
    }
}
