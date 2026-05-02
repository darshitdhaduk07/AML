package com.tss.aml.service;

import com.tss.aml.master.entity.BlacklistedToken;
import com.tss.aml.master.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BlacklistedTokenRepository repository;

    @Transactional
    public void blacklist(Claims claims, UUID userId) {
        repository.saveToMaster(
                UUID.randomUUID(),
                UUID.fromString(claims.getId()),
                userId,
                claims.get("tenantId", String.class),
                LocalDateTime.now(),
                claims.getExpiration().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
    }

    public boolean isBlacklisted(UUID jti) {
        return repository.existsByJti(jti);
    }

    // Runs every night at 2 AM — deletes rows whose token already expired naturally
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void purgeExpired() {
        repository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}