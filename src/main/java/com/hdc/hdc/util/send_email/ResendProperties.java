package com.hdc.hdc.util.send_email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "resend")
public record ResendProperties(@NotNull @Valid Api api, @NotNull @Valid Mail mail) {

    public record Api(
            @NotBlank String key,
            @NotBlank String baseUrl,
            @NotNull Duration connectTimeout,
            @NotNull Duration readTimeout) {
    }

    public record Mail(@NotBlank String from) {
    }
}
