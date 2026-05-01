package com.tss.aml.tenant.repository;

import com.tss.aml.enums.Role;
import com.tss.aml.tenant.entity.InAppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, UUID> {
    List<InAppNotification> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    List<InAppNotification> findByRecipientEmailAndRoleOrderByCreatedAtDesc(String recipientEmail, Role role);
}
