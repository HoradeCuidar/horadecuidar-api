package com.hdc.hdc.infra.bucket.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class R2Service {

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    private final S3Client r2;

    public R2Service(S3Client r2){
        this.r2 = r2;
    }

    public String upload(MultipartFile file) throws IOException {

        String filename = String.valueOf(UUID.randomUUID());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .contentType(file.getContentType())
                .build();

        r2.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return "https://pub-6fb926a37e0b421a8e958ac957ed6e8c.r2.dev/"
                + filename;
    }

    public void delete(String url) throws IOException {
        // Extrair o filename da URL (tudo após a última barra)
        String filename = url.substring(url.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();

        r2.deleteObject(deleteRequest);
    }
}