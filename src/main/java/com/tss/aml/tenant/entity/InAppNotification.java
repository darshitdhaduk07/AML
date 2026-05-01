package com.tss.aml.tenant.entity;

import com.tss.aml.enums.NotificationType;
import com.tss.aml.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "in_app_notifications")
@Getter
@Setter
public class InAppNotification extends BaseEntity {

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
