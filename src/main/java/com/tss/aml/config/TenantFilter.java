package com.tss.aml.config;

import com.tss.aml.tenat.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tenant = request.getHeader("X-Tenant-ID");

        if (tenant != null && !tenant.isEmpty()) {
            TenantContext.setTenant(tenant);
        }

        filterChain.doFilter(request, response);

        TenantContext.clear();
    }
}