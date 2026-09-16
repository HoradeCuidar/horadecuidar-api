package com.hdc.hdc.util.send_email;

public record EmailAttachment(
        String filename,
        String content,
        String contentType,
        String contentId) {
}
