package com.tss.aml.service;

import com.tss.aml.enums.NotificationType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Async
    @Override
    public void sendNotification(String to, NotificationType type, Map<String, Object> variables){

        try {
            Context context = new Context();
            context.setVariables(variables);

            String subject = "";
            String contentTemplate = "";

            switch (type) {
                case BANK_ADMIN_REGISTERED:
                    subject = "Welcome Bank Admin";
                    contentTemplate = "email/bank-admin-welcome";
                    break;
                case CO_REGISTERED:
                    subject = "Welcome Compliance Officer";
                    contentTemplate = "email/co-welcome";
                    break;
            }

            context.setVariable("contentTemplate", contentTemplate);
            context.setVariable("title", subject);
            String html = templateEngine.process(contentTemplate, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);

            log.info("Email sent successfully | to={} | type={}", to, type);

        } catch (Exception e) {
            log.error("Email sending failed | to={} | type={}", to, type, e);
        }
    }
}