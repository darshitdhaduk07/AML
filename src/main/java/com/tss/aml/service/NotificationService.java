package com.tss.aml.service;


import com.tss.aml.enums.NotificationType;

import java.util.Map;

public interface NotificationService {
    void sendNotification(String to, NotificationType type, Map<String, Object> variables);
}