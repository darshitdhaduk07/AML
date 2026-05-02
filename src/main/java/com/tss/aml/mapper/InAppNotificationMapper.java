package com.tss.aml.mapper;

import com.tss.aml.dto.result.InAppNotificationResponseDto;
import com.tss.aml.tenant.entity.InAppNotification;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationMapper {
    public InAppNotificationResponseDto toDto(InAppNotification notification) {
        InAppNotificationResponseDto dto = new InAppNotificationResponseDto();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
