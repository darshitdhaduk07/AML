package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.tenant.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.Map;

//Per [Duration] Day/s Transaction Frequency [Greater] than [threshold]
@Service
public class RL_002 implements CustomerRuleTemplate {
    @Override
    public boolean check(Customer customer, Map<String, Object> parameters) {
        return false;
    }
}