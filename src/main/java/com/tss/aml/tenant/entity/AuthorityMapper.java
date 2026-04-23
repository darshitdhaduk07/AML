package com.tss.aml.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
public class AuthorityMapper extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "co_officer_id")
    private ComplianceOfficer complianceOfficer;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    private Authority authority;

}
