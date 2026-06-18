package com.tss.aml.authentication;

import com.tss.aml.model.AppUser;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class TenantAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;

    private String tenant;
    private String role;

    // BEFORE LOGIN
    public TenantAuthenticationToken(String email, String password, String tenant, String role) {
        super(List.of());

        this.principal = email;
        this.credentials = password;
        this.tenant = tenant;
        this.role = role;

        setAuthenticated(false);
    }

    // AFTER SUCCESS
    public TenantAuthenticationToken(AppUser principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        this.principal = principal;
        this.credentials = null;

        setAuthenticated(true);
    }
}