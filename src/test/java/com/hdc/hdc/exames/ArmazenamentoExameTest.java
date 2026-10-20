package com.hdc.hdc.exames;

import com.hdc.hdc.util.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArmazenamentoExameTest {
    @Test
    void bucketAusenteRetornaErroDeServicoSemExporMensagemDoSdk() {
        S3Client s3 = mock(S3Client.class);
        NoSuchBucketException ausente = NoSuchBucketException.builder()
                .message("The specified bucket does not exist")
                .build();
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(ausente);

        ArmazenamentoExame storage = new ArmazenamentoExame(s3, "hdc-exames", "hdc-images");
        ArmazenamentoExameException erro = assertThrows(ArmazenamentoExameException.class,
                () -> storage.gravar(new byte[]{1, 2, 3}));
        assertTrue(erro.getMessage().contains("EXAMES_R2_BUCKET"));
        var response = new GlobalExceptionHandler().handleArmazenamentoExameException(erro);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("arquivo", response.getBody().field());
        assertFalse(response.getBody().message().contains("The specified bucket"));
    }
}
