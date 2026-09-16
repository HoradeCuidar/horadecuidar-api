package com.hdc.hdc.util.send_email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SendEmailRequest(
    String from,
    List<String> to,
    String subject,
    String html,
    @JsonProperty("reply_to") String replyTo,
    List<SendEmailAttachmentRequest> attachments
) {}
