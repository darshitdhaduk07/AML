package com.tss.aml.service;

import com.tss.aml.enums.Role;
import com.tss.aml.model.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.*;

@Service
@Slf4j
public class JwtService {
    @Autowired
    private BlacklistService blacklistService;
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry-ms}")
    private long expiryMs;

    public String generateToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        if (user.getRole() != Role.SYSTEM_ADMIN) {
            claims.put("tenant", user.getTenant().substring(7));
        }

        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())      // jti
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean verifyToken(String token) {
        log.debug("Verifying token: {}", token);
        Claims claims;
        try {
            claims = this.extractAllClaims(token);
        } catch (Exception e) {
            log.warn("Token verification failed: {}", e.getMessage());
            return false;
        }

        return !blacklistService.isBlacklisted(UUID.fromString(claims.getId()));
    }
}