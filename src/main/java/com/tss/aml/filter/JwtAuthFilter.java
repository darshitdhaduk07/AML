package com.tss.aml.filter;

import com.tss.aml.authentication.TenantAuthenticationToken;
import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.LoginRequestDto;
import com.tss.aml.enums.Role;
import com.tss.aml.model.AppUser;
import com.tss.aml.service.BlacklistService;
import com.tss.aml.service.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private BlacklistService blacklistService;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        Claims claims;

        // Step 1 — parse and verify signature + expiry
        try {
            claims = jwtService.extractAllClaims(token);
        } catch (ExpiredJwtException e) {
            sendError(res, "TOKEN_EXPIRED", "Access token has expired");
            return;
        } catch (SignatureException | MalformedJwtException e) {
            sendError(res, "TOKEN_INVALID", "Token signature is invalid");
            return;
        } catch (UnsupportedJwtException e) {
            sendError(res, "TOKEN_UNSUPPORTED", "Token format not supported");
            return;
        } catch (Exception e) {
            sendError(res, "TOKEN_BAD", "Could not process token");
            return;
        }

        // Step 2 — check blacklist (logout detection)
        if (blacklistService.isBlacklisted(UUID.fromString(claims.getId()))) {
            sendError(res, "TOKEN_REVOKED", "Token has been revoked");
            return;
        }

        // Step 3 — extract role and route tenant context
        String role = claims.get("role", String.class);
        String tenant = claims.get("tenant", String.class);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        if ("SYSTEM_ADMIN".equals(role)) {
            TenantContext.setTenant("master");

        } else if ("BANK_ADMIN".equals(role)) {
            if (tenant == null || tenant.isBlank()) {
                sendError(res, "TOKEN_INVALID", "Missing tenantId for BANK_ADMIN");
                return;
            }
            TenantContext.setTenant(tenant);

        } else if ("COMPLIANCE_OFFICER".equals(role)) {
            if (tenant == null || tenant.isBlank()) {
                sendError(res, "TOKEN_INVALID", "Missing tenantId for COMPLIANCE_OFFICER");
                return;
            }
            TenantContext.setTenant(tenant);

            List<String> auths = claims.get("authorities", List.class);
            if (auths == null || auths.isEmpty()) {
                sendError(res, "TOKEN_INVALID", "COMPLIANCE_OFFICER must have authorities");
                return;
            }
            auths.forEach(a -> authorities.add(new SimpleGrantedAuthority("AUTH_" + a)));

        } else {
            sendError(res, "TOKEN_INVALID", "Unknown role: " + role);
            return;
        }

        // Step 4 — set Spring Security context

        AppUser dto = new AppUser();

        dto.setUsername(claims.get("sub").toString());
        dto.setRole(Role.valueOf(claims.get("role").toString()));
        if (claims.get("tenant") != null) dto.setTenantId(claims.get("tenant").toString());
        dto.setAuthorities(List.of(new SimpleGrantedAuthority("Role_" + role)));

        TenantAuthenticationToken auth = new TenantAuthenticationToken(dto, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear(); // always clean up ThreadLocal
        }
    }

    private void sendError(HttpServletResponse res, String code, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write(String.format("{\"error\":\"%s\",\"message\":\"%s\"}", code, message));
    }
}