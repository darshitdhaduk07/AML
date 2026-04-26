package com.tss.aml.filter;

import com.tss.aml.authentication.TenantAuthenticationToken;
import com.tss.aml.context.TenantContext;
import com.tss.aml.dto.request.LoginRequestDto;
import com.tss.aml.model.AppUser;
import com.tss.aml.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Component
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private JwtService jwtService;

    public LoginAuthenticationFilter(AuthenticationManager manager) throws NoSuchAlgorithmException {
        super("/login");
        setAuthenticationManager(manager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, @NonNull HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        LoginRequestDto body = mapper.readValue(request.getInputStream(), LoginRequestDto.class);

        if (body.getRole().equals("SYSTEM_ADMIN")) {
            TenantContext.setTenant("master");
        } else {
            TenantContext.setTenant("tenant_" + body.getTenant());
        }

        TenantAuthenticationToken token =
                new TenantAuthenticationToken(
                        body.getEmail(),
                        body.getPassword(),
                        "tenant_" + body.getTenant(),
                        body.getRole()
                );

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull FilterChain chain, @NonNull Authentication auth) throws IOException {

        response.setContentType("application/json");

        response.getWriter().write("""
                {
                  "message":"Login Success",
                  "jwt":\"""" + jwtService.generateToken((AppUser) auth.getPrincipal()) + "\"\n}");
    }

    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull AuthenticationException ex
    ) throws IOException {

        response.setStatus(401);

        response.getWriter().write("""
                {
                  "error":"Invalid Credentials"
                }
                """);
    }
}