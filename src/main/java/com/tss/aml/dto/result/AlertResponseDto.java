package com.tss.aml.dto.result;

import com.tss.aml.enums.RuleType;
import lombok.Data;

import java.util.UUID;

@Data
public class AlertResponseDto {
    private UUID id;
    private String ruleCode;
    private TransactionResponseDto transaction;
    private String customer_number;
    private String ruleDescription;
    private UUID group_id;
    private Boolean active;
    private Boolean falsePositive;
    private RuleType ruleType;
    private int weight;
}
