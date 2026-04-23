package com.tss.aml.config;

import com.tss.aml.authentication.CustomAuthenticationProvider;
import com.tss.aml.filter.JwtAuthFilter;
import com.tss.aml.filter.LoginAuthenticationFilter;
import com.tss.aml.filter.TenantFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider provider;

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, AuthenticationManager manager, LoginAuthenticationFilter loginAuthenticationFilter, JwtAuthFilter jwtAuthFilter, TenantFilter tenantFilter) throws Exception {

        loginAuthenticationFilter.setAuthenticationManager(manager);

        http.csrf(csrf -> csrf.disable())

                .authenticationProvider(provider)

                .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterAt(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/register")
                        .permitAll()
                        .anyRequest()
                        .authenticated());

        return http.build();
    }
}