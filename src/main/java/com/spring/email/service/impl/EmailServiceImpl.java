package com.spring.email.service.impl;

import com.spring.email.service.EmailService;
import com.spring.email.utils.EmailUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

import static com.spring.email.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailTemplate";
    public static final String TEXT_HTML = "text/html";
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final TemplateEngine templateEngine;

    private final JavaMailSender emailSender;

    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name, host, token));
            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithAttachments(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));

            FileSystemResource file1 = new FileSystemResource(new File(System.getProperty("user.home") + "/Desktop/file1.png"));
            helper.addAttachment(file1.getFilename(), file1);

            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMimeMessageWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));

            FileSystemResource file1 = new FileSystemResource(new File(System.getProperty("user.home") + "/Desktop/file1.png"));
            helper.addInline(getContentId(file1.getFilename()), file1);

            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", getVerificationUrl(host,token));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);

            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailsWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(to);

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("url", getVerificationUrl(host,token));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);

            //Add HTML
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, TEXT_HTML);
            mimeMultipart.addBodyPart(messageBodyPart);

            //Add image
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") + "/Desktop/file1.png");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(imageBodyPart);

            message.setContent(mimeMultipart);

            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }

}
