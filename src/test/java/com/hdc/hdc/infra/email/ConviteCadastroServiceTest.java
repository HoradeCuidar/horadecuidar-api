package com.hdc.hdc.infra.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConviteCadastroServiceTest {

    @Test
    void informaQuandoOProvedorAceitaOConvite() {
        var email = mock(EmailMontagemService.class);
        var service = new ConviteCadastroService(email);

        assertThat(service.enviar("Maria", "maria@example.com", "maria", "senha123")).isTrue();
        verify(email).enviarConfirmacaoCadastro("Maria", "maria@example.com", "maria", "senha123");
    }

    @Test
    void informaFalhaSemDesfazerOCadastroJaSalvo() {
        var email = mock(EmailMontagemService.class);
        doThrow(new IllegalStateException("Serviço indisponível"))
                .when(email).enviarConfirmacaoCadastro("Maria", "maria@example.com", "maria", "senha123");

        assertThat(new ConviteCadastroService(email)
                .enviar("Maria", "maria@example.com", "maria", "senha123")).isFalse();
    }
}
