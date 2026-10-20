package com.hdc.hdc.util.send_email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdc.hdc.util.send_email.dto.SendEmailAttachmentRequest;
import com.hdc.hdc.util.send_email.dto.SendEmailRequest;
import com.hdc.hdc.util.send_email.dto.SendEmailResponse;
import com.hdc.hdc.util.exception.FailedSendEmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.nio.charset.StandardCharsets;

@Service
public class ResendEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);
    private static final int MAX_ERROR_DETAIL_LENGTH = 500;

    private final RestClient resendRestClient;
    private final String defaultFrom;
    private final ObjectMapper objectMapper;

    public ResendEmailService(
            @Qualifier("resendRestClient") RestClient resendRestClient,
            ResendProperties properties,
            ObjectMapper objectMapper) {
        this.resendRestClient = resendRestClient;
        this.defaultFrom = properties.mail().from();
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        sendEmail(List.of(to), subject, htmlBody);
    }

    @Override
    public void sendEmail(List<String> recipients, String subject, String htmlBody) {
        sendEmail(recipients, subject, htmlBody, List.of());
    }

    @Override
    public void sendEmail(List<String> recipients, String subject, String htmlBody, List<EmailAttachment> attachments) {
        if (recipients == null || recipients.isEmpty() || recipients.size() > 50 || recipients.stream().anyMatch(recipient -> recipient == null || recipient.isBlank())) {
            throw new IllegalArgumentException("Informe entre 1 e 50 destinatários.");
        }
        if (subject == null || subject.isBlank() || htmlBody == null) {
            throw new IllegalArgumentException("Assunto e conteúdo HTML do e-mail são obrigatórios.");
        }
        List<EmailAttachment> safeAttachments = attachments == null ? List.of() : attachments;

        SendEmailRequest request = new SendEmailRequest(
            defaultFrom,
            recipients,
            subject,
            htmlBody,
            null,
            safeAttachments.stream()
                    .map(attachment -> new SendEmailAttachmentRequest(
                            attachment.filename(), attachment.content(), attachment.contentType(), attachment.contentId()))
                    .toList()
        );

        try {
            SendEmailResponse response = resendRestClient.post()
                .uri("/emails")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    logResendError(resp.getStatusCode(), new String(resp.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new FailedSendEmailException("email");
                })
                .body(SendEmailResponse.class);

            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new FailedSendEmailException("email", "A Resend não retornou o identificador do e-mail enviado.");
            }

            log.info("E-mail aceito pela Resend. ID da mensagem: {}", response.id());
        } catch (FailedSendEmailException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Não foi possível comunicar com a Resend.", e);
            throw new FailedSendEmailException("email");
        }
    }

    private void logResendError(HttpStatusCode status, String responseBody) {
        try {
            JsonNode error = objectMapper.readTree(responseBody);
            String code = error.path("name").asText(error.path("error").asText("não informado"));
            String message = error.path("message").asText("não informado");
            log.error("Resend rejeitou o envio do e-mail. Status: {}. Código: {}. Detalhe: {}",
                    status, sanitizeForLog(code), sanitizeForLog(message));
        } catch (JsonProcessingException e) {
            log.error("Resend rejeitou o envio do e-mail. Status: {}. A resposta não continha um erro JSON legível.", status);
        }
    }

    private String sanitizeForLog(String value) {
        if (value == null || value.isBlank()) {
            return "não informado";
        }

        String sanitized = value
                .replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("(?i)\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b", "***@***")
                .replaceAll("\\bre_[A-Za-z0-9_]+\\b", "re_***");
        return sanitized.length() > MAX_ERROR_DETAIL_LENGTH
                ? sanitized.substring(0, MAX_ERROR_DETAIL_LENGTH) + "…"
                : sanitized;
    }
}
