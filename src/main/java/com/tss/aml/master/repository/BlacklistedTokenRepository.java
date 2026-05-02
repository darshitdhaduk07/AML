package com.tss.aml.master.repository;

import com.tss.aml.master.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {
    @Query(
            value = "SELECT COUNT(1) > 0 FROM master.blacklisted_tokens WHERE jti = :jti",
            nativeQuery = true
    )
    boolean existsByJti(@Param("jti") UUID jti);
    @org.springframework.data.jpa.repository.Modifying
    @Query(
            value = "INSERT INTO master.blacklisted_tokens (id, jti, user_id, tenant_id, blacklisted_at, expires_at) " +
                    "VALUES (:id, :jti, :userId, :tenantId, :blacklistedAt, :expiresAt)",
            nativeQuery = true
    )
    void saveToMaster(
            @Param("id") UUID id,
            @Param("jti") UUID jti,
            @Param("userId") UUID userId,
            @Param("tenantId") String tenantId,
            @Param("blacklistedAt") LocalDateTime blacklistedAt,
            @Param("expiresAt") LocalDateTime expiresAt
    );

    @org.springframework.data.jpa.repository.Modifying
    @Query(
            value = "DELETE FROM master.blacklisted_tokens WHERE expires_at < :threshold",
            nativeQuery = true
    )
    void deleteAllByExpiresAtBefore(@Param("threshold") LocalDateTime threshold);
}