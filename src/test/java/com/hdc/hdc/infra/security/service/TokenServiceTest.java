package com.hdc.hdc.infra.security.service;

import com.auth0.jwt.JWT;
import com.hdc.hdc.usuarios.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    @Test
    void tokenExpiraAposUmMesDeCalendario() {
        var service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "segredo-de-teste-com-tamanho-suficiente");
        var usuario = new Usuario();
        usuario.setUsername("maria");
        Instant antes = Instant.now();

        var token = service.generatedToken(usuario);

        Instant depois = Instant.now();
        Instant expiracao = JWT.decode(token).getExpiresAtAsInstant();
        assertThat(expiracao).isBetween(
                ZonedDateTime.ofInstant(antes, ZoneOffset.UTC).plusMonths(1).toInstant().minusSeconds(1),
                ZonedDateTime.ofInstant(depois, ZoneOffset.UTC).plusMonths(1).toInstant().plusSeconds(1));
        assertThat(service.validateToken(token)).isEqualTo("maria");
    }
}
