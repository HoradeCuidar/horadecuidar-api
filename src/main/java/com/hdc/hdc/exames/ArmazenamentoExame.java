package com.hdc.hdc.exames;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.util.UUID;

@Component
public class ArmazenamentoExame {
    private final S3Client s3;
    private final String bucket;

    public ArmazenamentoExame(S3Client s3,
                             @Value("${cloudflare.r2.exames-bucket}") String bucket,
                             @Value("${cloudflare.r2.bucket}") String imagensBucket) {
        if (bucket.equals(imagensBucket)) {
            throw new IllegalArgumentException("O bucket privado de exames deve ser diferente do bucket público de imagens");
        }
        this.s3 = s3;
        this.bucket = bucket;
    }

    public String gravar(byte[] conteudo) {
        String chave = "exames/" + UUID.randomUUID() + ".pdf";
        try {
            s3.putObject(PutObjectRequest.builder().bucket(bucket).key(chave)
                    .contentType("application/pdf").build(), RequestBody.fromBytes(conteudo));
            return chave;
        } catch (SdkException e) {
            throw indisponivel(e);
        }
    }

    public byte[] ler(String chave) {
        try {
            return s3.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(chave).build()).asByteArray();
        } catch (SdkException e) {
            throw indisponivel(e);
        }
    }

    public void apagar(String chave) {
        try {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(chave).build());
        } catch (SdkException e) {
            throw indisponivel(e);
        }
    }
    private ArmazenamentoExameException indisponivel(SdkException cause) {
        if (cause instanceof NoSuchBucketException ||
                cause instanceof S3Exception s3Exception && s3Exception.awsErrorDetails() != null &&
                        "NoSuchBucket".equals(s3Exception.awsErrorDetails().errorCode())) {
            return new ArmazenamentoExameException(
                    "Bucket privado de exames indisponível. Verifique EXAMES_R2_BUCKET e a configuração do R2.", cause);
        }
        return new ArmazenamentoExameException("Armazenamento de exames indisponível. Tente novamente mais tarde.", cause);
    }
}
