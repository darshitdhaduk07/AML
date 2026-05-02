package com.tss.aml.tenant.repository;

import com.tss.aml.enums.Role;
import com.tss.aml.tenant.entity.InAppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, UUID> {

    Page<InAppNotification> findByRecipientEmailAndRoleAndIsReadFalseOrderByCreatedAtDesc(String recipientEmail, Role role, Pageable pageable);
}
