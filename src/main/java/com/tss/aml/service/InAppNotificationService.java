package com.tss.aml.service;

import com.tss.aml.dto.result.InAppNotificationResponseDto;
import com.tss.aml.dto.result.PaginatedResponseDto;
import com.tss.aml.enums.NotificationType;
import com.tss.aml.enums.Role;
import com.tss.aml.exception.ResourceNotFoundException;
import com.tss.aml.tenant.entity.InAppNotification;
import com.tss.aml.tenant.repository.InAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InAppNotificationService {

    private final InAppNotificationRepository inAppNotificationRepository;
    private final AppUserService appUserService;

    public void createNotification(String recipientEmail, Role role, NotificationType type, String message) {
        InAppNotification notification = new InAppNotification();
        notification.setRecipientEmail(recipientEmail);
        notification.setRole(role);
        notification.setType(type);
        notification.setMessage(message);
        inAppNotificationRepository.save(notification);
        log.info("In-app notification sent to {} with role {}: [{}] {}", recipientEmail, role, type, message);
    }

    public PaginatedResponseDto<InAppNotificationResponseDto> getNotificationsForCurrentUser(Pageable pageable) {
        String email = appUserService.get().getUsername();
        Page<InAppNotification> notificationPage = inAppNotificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email, pageable);
        
        return PaginatedResponseDto.<InAppNotificationResponseDto>builder()
                .content(notificationPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList()))
                .pageNumber(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .last(notificationPage.isLast())
                .build();
    }

    public PaginatedResponseDto<InAppNotificationResponseDto> getNotificationsForRole(Role role, Pageable pageable) {
        String email = appUserService.get().getUsername();
        Page<InAppNotification> notificationPage = inAppNotificationRepository.findByRecipientEmailAndRoleOrderByCreatedAtDesc(email, role, pageable);

        return PaginatedResponseDto.<InAppNotificationResponseDto>builder()
                .content(notificationPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList()))
                .pageNumber(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .last(notificationPage.isLast())
                .build();
    }

    public void markAsRead(UUID notificationId) {
        InAppNotification notification = inAppNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        
        String currentUserEmail = appUserService.get().getUsername();
        if (!notification.getRecipientEmail().equals(currentUserEmail)) {
            throw new IllegalArgumentException("Unauthorized to mark this notification as read");
        }

        notification.setRead(true);
        inAppNotificationRepository.save(notification);
    }

    private InAppNotificationResponseDto convertToDto(InAppNotification notification) {
        InAppNotificationResponseDto dto = new InAppNotificationResponseDto();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
