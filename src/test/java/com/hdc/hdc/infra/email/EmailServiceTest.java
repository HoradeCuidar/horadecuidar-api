package com.hdc.hdc.infra.email;

import com.hdc.hdc.util.send_email.EmailAttachment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Test
    void deveConverterRecursoClasspathEmAnexoInlineDoResend() {
        com.hdc.hdc.util.send_email.EmailService sender = mock(com.hdc.hdc.util.send_email.EmailService.class);
        EmailService service = new EmailService(sender);

        service.enviarHtml("pessoa@example.com", "Assunto", "<img src=\"cid:logoImage\">",
                Map.of("logoImage", "static/logo-hdc.png"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EmailAttachment>> attachments = ArgumentCaptor.forClass(List.class);
        verify(sender).sendEmail(org.mockito.ArgumentMatchers.eq(List.of("pessoa@example.com")),
                org.mockito.ArgumentMatchers.eq("Assunto"),
                org.mockito.ArgumentMatchers.eq("<img src=\"cid:logoImage\">"), attachments.capture());
        EmailAttachment logo = attachments.getValue().getFirst();
        assertThat(logo.filename()).isEqualTo("logo-hdc.png");
        assertThat(logo.contentType()).isEqualTo("image/png");
        assertThat(logo.content()).isNotBlank();
        assertThat(logo.contentId()).isEqualTo("logoImage");
    }
}
