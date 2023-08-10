package com.spring.email.service;

public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);

    void sendMimeMessageWithAttachments(String name, String to, String token);

    void sendMimeMessageWithEmbeddedFiles(String name, String to, String token);

    void sendHtmlEmail(String name, String to, String token);

    void sendHtmlEmailsWithEmbeddedFiles(String name, String to, String token);

}
