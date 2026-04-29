package com.tss.aml.controller;

import com.tss.aml.enums.NotificationType;
import com.tss.aml.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class NotificationTest {
    private final NotificationService notificationService;
    @PostMapping("/send")
    public void sendMail()
    {
//        try{
//            Map<String, Object> variables = new HashMap<>();
//            notificationService.sendNotification("darshitcoc123@gmail.com", NotificationType.TEST, variables);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

    }
    @GetMapping("/hello")
    public ResponseEntity sayhello()
    {
        return ResponseEntity.status(400).body("hello");
    }
}
