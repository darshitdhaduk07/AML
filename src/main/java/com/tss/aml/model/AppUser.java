package com.tss.aml.model;


import com.tss.aml.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class AppUser implements UserDetails {

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String tenant; // null for SYSTEM_ADMIN

    @ElementCollection(fetch = FetchType.EAGER)
    private List<SimpleGrantedAuthority> authorities; // ["MAKER", "CHECKER"] for COMPLIANCE_OFFICER

    @Override public boolean isAccountNonExpired()   { return true; }
    @Override public boolean isAccountNonLocked()    { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()             { return true; }

}