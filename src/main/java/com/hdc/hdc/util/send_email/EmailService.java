package com.hdc.hdc.util.send_email;

import java.util.List;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlBody);
    void sendEmail(List<String> recipients, String subject, String htmlBody);
    void sendEmail(List<String> recipients, String subject, String htmlBody, List<EmailAttachment> attachments);
}
