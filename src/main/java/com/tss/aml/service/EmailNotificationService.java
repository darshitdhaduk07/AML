package com.tss.aml.service;

import com.tss.aml.enums.NotificationType;
import jakarta.mail.MessagingException;
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

//    @Override
//    public void sendNotification(String to, String subject, String htmlContent) throws Exception {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlContent, true);
//
//        mailSender.send(message);
//    }


    @Async
    @Override
    public void sendNotification(String to, NotificationType type, Map<String, Object> variables) throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);

        // Map template + subject
        String template = "";
        String subject = "";
        String contentTemplate = "";

        switch (type) {
//            case INFO:
//                subject = (String)context.getVariable("subject");
//                contentTemplate = "email/infoLayout";
//                break;
        }
        context.setVariable("contentTemplate", contentTemplate);
        context.setVariable("title", subject);
        String html = templateEngine.process("email/layout", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(message);
    }
}