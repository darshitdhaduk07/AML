package com.tss.aml.config;

import com.tss.aml.authentication.CustomAuthenticationProvider;
import com.tss.aml.filter.JwtAuthFilter;
import com.tss.aml.filter.LoginAuthenticationFilter;
import com.tss.aml.filter.TenantFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider provider;

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, AuthenticationManager manager, LoginAuthenticationFilter loginAuthenticationFilter, JwtAuthFilter jwtAuthFilter, TenantFilter tenantFilter) throws Exception {

        loginAuthenticationFilter.setAuthenticationManager(manager);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authenticationProvider(provider)

                .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterAt(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login").permitAll()
                        .anyRequest()
                        .authenticated());

        return http.build();
    }
}