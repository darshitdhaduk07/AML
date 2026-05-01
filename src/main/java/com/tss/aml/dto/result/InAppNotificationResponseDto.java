package com.tss.aml.dto.result;

import com.tss.aml.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InAppNotificationResponseDto {
    private UUID id;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
