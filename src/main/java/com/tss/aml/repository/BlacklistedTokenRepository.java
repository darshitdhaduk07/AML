package com.tss.aml.repository;

import com.tss.aml.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {
    boolean existsByJti(UUID jti);
    void deleteAllByExpiresAtBefore(LocalDateTime threshold);
}