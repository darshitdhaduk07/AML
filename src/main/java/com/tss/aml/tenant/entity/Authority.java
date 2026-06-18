package com.tss.aml.tenant.entity;

import com.tss.aml.enums.AuthorityName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Authority extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private AuthorityName authorityName;

}
