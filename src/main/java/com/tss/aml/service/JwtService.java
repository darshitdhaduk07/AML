package com.tss.aml.service;

import com.tss.aml.enums.Role;
import com.tss.aml.model.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class JwtService {
    @Autowired
    private BlacklistService blacklistService;
//    @Value("${jwt.secret}")
    private String secret;

    public JwtService() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey sk = keyGenerator.generateKey();
        secret = Base64.getEncoder().encodeToString(sk.getEncoded());
    }

    @Value("${jwt.expiry-ms}")
    private long expiryMs;

    public String generateToken(AppUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        if (user.getRole() != Role.SYSTEM_ADMIN) {
            claims.put("tenant", user.getTenant().substring(7));
        }

//        if (user.getRole() == Role.COMPLIANCE_OFFICER) {
//            claims.put("authorities", user.getAuthorities());
//        }

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
        System.out.println(token);
        Claims claims;
        try {
            claims = this.extractAllClaims(token);
        } catch (Exception e) {
            return false;
        }

        return !blacklistService.isBlacklisted(UUID.fromString(claims.getId()));
    }
}