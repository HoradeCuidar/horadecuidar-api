package com.hdc.hdc.infra.email;

import lombok.RequiredArgsConstructor;
import com.hdc.hdc.util.exception.FailedSendEmailException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final com.hdc.hdc.util.send_email.EmailService emailService;

    public void enviarHtml(String email, String assunto, String html, Map<String, String> inlineResources) {
        try {
            emailService.sendEmail(List.of(email), assunto, html, toInlineAttachments(inlineResources));
        } catch (IOException e) {
            throw new FailedSendEmailException("email", "Não foi possível preparar os recursos do e-mail.");
        }
    }

    private List<com.hdc.hdc.util.send_email.EmailAttachment> toInlineAttachments(Map<String, String> inlineResources)
            throws IOException {
        if (inlineResources == null || inlineResources.isEmpty()) {
            return List.of();
        }

        List<com.hdc.hdc.util.send_email.EmailAttachment> attachments = new ArrayList<>();
        for (Map.Entry<String, String> entry : inlineResources.entrySet()) {
            ClassPathResource resource = new ClassPathResource(entry.getValue());
            String filename = resource.getFilename();
            if (filename == null || !resource.exists()) {
                throw new IOException("Recurso inline não encontrado.");
            }
            byte[] bytes;
            try (var inputStream = resource.getInputStream()) {
                bytes = inputStream.readAllBytes();
            }
            String content = Base64.getEncoder().encodeToString(bytes);
            String contentType = MediaTypeFactory.getMediaType(filename)
                    .map(MediaType::toString)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            attachments.add(new com.hdc.hdc.util.send_email.EmailAttachment(filename, content, contentType, entry.getKey()));
        }
        return List.copyOf(attachments);
    }
}
