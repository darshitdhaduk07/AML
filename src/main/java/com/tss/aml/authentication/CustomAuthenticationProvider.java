package com.tss.aml.authentication;

import com.tss.aml.context.TenantContext;
import com.tss.aml.enums.Role;
import com.tss.aml.master.entity.SystemAdmin;
import com.tss.aml.model.AppUser;
import com.tss.aml.tenant.entity.BankAdmin;
import com.tss.aml.tenant.entity.ComplianceOfficer;
import com.tss.aml.tenant.repository.BankAdminRepository;
import com.tss.aml.tenant.repository.ComplianceOfficerRepository;
import com.tss.aml.master.repository.SystemAdminRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BankAdminRepository bankAdminRepo;
    private final SystemAdminRepository systemAdminRepo;
    private final ComplianceOfficerRepository complianceOfficerRepo;

    @Override
    public Authentication authenticate(@NonNull Authentication auth) {
        TenantAuthenticationToken token = (TenantAuthenticationToken) auth;

        String email = token.getPrincipal().toString();

        String password = token.getCredentials().toString();

        Role role = Role.valueOf(token.getRole());

        List<SimpleGrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        AppUser user = new AppUser();
        user.setAuthorities(grantedAuthorities);
        user.setRole(role);
        user.setUsername(email);
        user.setTenantId(TenantContext.getTenant());

        switch(role) {
            case BANK_ADMIN:
                BankAdmin bankAdmin = bankAdminRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
                if (!password.matches(bankAdmin.getPassword()))
                    throw new BadCredentialsException("Wrong Credentials");
                break;
            case SYSTEM_ADMIN:
                SystemAdmin systemAdmin = systemAdminRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
                if (!password.matches(systemAdmin.getPassword()))
                    throw new BadCredentialsException("Wrong Credentials");
                break;
            case COMPLIANCE_OFFICER:
                ComplianceOfficer complianceOfficer = complianceOfficerRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
                if (!password.matches(complianceOfficer.getPassword()))
                    throw new BadCredentialsException("Wrong Credentials");
                break;
            default:
                throw new IllegalArgumentException("Invalid Role");
        }

        return new TenantAuthenticationToken(
                user,
                grantedAuthorities
        );
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return TenantAuthenticationToken.class.isAssignableFrom(clazz);
    }
}