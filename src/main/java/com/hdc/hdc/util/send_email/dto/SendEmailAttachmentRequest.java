package com.hdc.hdc.util.send_email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendEmailAttachmentRequest(
        String filename,
        String content,
        @JsonProperty("content_type") String contentType,
        @JsonProperty("content_id") String contentId) {
}
