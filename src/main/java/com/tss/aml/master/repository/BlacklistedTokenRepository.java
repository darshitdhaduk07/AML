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
    void deleteAllByExpiresAtBefore(LocalDateTime threshold);
}