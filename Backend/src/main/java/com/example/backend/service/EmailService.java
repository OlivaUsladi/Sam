package com.example.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            Context ctx = new Context();
            ctx.setVariable("resetLink", frontendBaseUrl + "/reset-password?token=" + resetToken);
            ctx.setVariable("ttlMinutes", 15);

            String html = templateEngine.process("password-reset", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("SAM — Восстановление пароля");
            helper.setText(html, true);
            mailSender.send(message);

            log.info("Password reset email sent to {}", toEmail.substring(0, 3) + "***");
        } catch (MessagingException e) {
            log.error("Failed to send password reset email", e);
        }
    }

    @Async
    public void sendFeedbackNotification(String userName, String userEmail, String subject, String messageText) {
        try {
            Context ctx = new Context();
            ctx.setVariable("userName", userName);
            ctx.setVariable("userEmail", userEmail);
            ctx.setVariable("subject", subject);
            ctx.setVariable("messageText", messageText);

            String html = templateEngine.process("feedback-notification", ctx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(fromAddress);
            helper.setSubject("SAM Обратная связь: " + subject);
            helper.setReplyTo(userEmail);
            helper.setText(html, true);
            mailSender.send(message);

            log.info("Feedback notification sent");
        } catch (MessagingException e) {
            log.error("Failed to send feedback notification", e);
        }
    }
}
