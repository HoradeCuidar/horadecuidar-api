package com.hdc.hdc.util.send_email;

import com.hdc.hdc.util.exception.FailedSendEmailException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ResendEmailServiceTest {

    private MockRestServiceServer server;
    private ResendEmailService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer test-key");
        server = MockRestServiceServer.bindTo(builder).build();
        ResendProperties properties = new ResendProperties(
                new ResendProperties.Api("test-key", "https://api.resend.com", Duration.ofSeconds(1), Duration.ofSeconds(1)),
                new ResendProperties.Mail("Hora de Cuidar <nao-responda@example.com>"));
        service = new ResendEmailService(builder.build(), properties, new ObjectMapper());
    }

    @Test
    void deveEnviarPayloadCompativelComResendIncluindoImagemInline() {
        server.expect(once(), requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-key"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "from":"Hora de Cuidar <nao-responda@example.com>",
                          "to":["pessoa@example.com"],
                          "subject":"Assunto",
                          "html":"<img src=\\"cid:logoImage\\">",
                          "attachments":[{
                            "filename":"logo-hdc.png",
                            "content":"aGVsbG8=",
                            "content_type":"image/png",
                            "content_id":"logoImage"
                          }]
                        }
                        """))
                .andRespond(withSuccess("{\"id\":\"email-id\"}", MediaType.APPLICATION_JSON));

        service.sendEmail(List.of("pessoa@example.com"), "Assunto", "<img src=\"cid:logoImage\">",
                List.of(new EmailAttachment("logo-hdc.png", "aGVsbG8=", "image/png", "logoImage")));

        server.verify();
    }

    @Test
    void deveConverterErroDaResendEmExcecaoDeDominio() {
        server.expect(requestTo("https://api.resend.com/emails"))
                .andRespond(withBadRequest()
                        .body("{\"name\":\"invalid_from_address\",\"message\":\"Invalid from field: pessoa@example.com\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> service.sendEmail("pessoa@example.com", "Assunto", "<p>Olá</p>"))
                .isInstanceOf(FailedSendEmailException.class);
    }
}
