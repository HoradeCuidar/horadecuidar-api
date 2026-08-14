package com.hdc.hdc.infra.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void enviarHtml(String email, String assunto, String html, java.util.Map<String, String> inlineResources) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(assunto);
            helper.setText(html, true);

            if (inlineResources != null) {
                for (java.util.Map.Entry<String, String> entry : inlineResources.entrySet()) {
                    ClassPathResource resource = new ClassPathResource(entry.getValue());
                    helper.addInline(entry.getKey(), resource);
                }
            }

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
