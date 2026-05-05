package com.tss.aml.mapper;

import com.tss.aml.dto.result.AlertResponseDto;
import com.tss.aml.dto.result.CustomerResponseDto;
import com.tss.aml.dto.result.TransactionResponseDto;
import com.tss.aml.rule_engine.RuleTemplateFactory;
import com.tss.aml.tenant.entity.BrokenRule;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerResponseDtoMapper {
    private final RuleTemplateFactory ruleTemplateFactory;
    private final com.tss.aml.tenant.repository.BrokenRuleRepository brokenRuleRepository;

    public CustomerResponseDto mapCustomer(Customer customer) {

        CustomerResponseDto dto = new CustomerResponseDto();

        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setFirstName(customer.getFirstName());
        dto.setMiddleName(customer.getMiddleName());
        dto.setLastName(customer.getLastName());
        dto.setFamilyCode(customer.getFamilyCode());
        dto.setDob(customer.getDob());
        dto.setOccupation(customer.getOccupation());
        dto.setNationalityCountry(customer.getNationalityCountry());
        dto.setCountryOfBirth(customer.getCountryOfBirth());
        dto.setIncome(customer.getIncome());
        dto.setNetWorth(customer.getNetWorth());
        
        Integer riskScore = brokenRuleRepository.calculateRiskScoreByCustomerNumber(customer.getCustomerNumber());
        dto.setRiskScore(riskScore != null ? riskScore : 0);

        return dto;
    }
    public TransactionResponseDto mapTransaction(Transaction t) {

        TransactionResponseDto dto = new TransactionResponseDto();

        dto.setTransactionNumber(t.getTransactionNumber());
        dto.setAmount(t.getAmount());
        dto.setTxnType(t.getTxnType());
        dto.setDirection(t.getDirection());
        dto.setTxnTime(t.getTxnTime());
        dto.setCountry(t.getCountry());
        dto.setAccountType(t.getAccountType());
        dto.setAccount(t.getAccount().getAccountNumber());
        dto.setIFSC(t.getIFSC());

        return dto;
    }

    public AlertResponseDto mapAlert(BrokenRule br) {
        AlertResponseDto dto = new AlertResponseDto();
        dto.setId(br.getId());
        dto.setActive(br.getActive());
        dto.setGroup_id(br.getGroup_id());
        dto.setRuleDescription(br.getRule().getDescription());
        dto.setCustomer_number(br.getCustomer().getCustomerNumber());
        dto.setTransaction(mapTransaction(br.getTransaction()));
        dto.setRuleType(ruleTemplateFactory.getRuleTemplate(br.getRule().getRuleCode()).getRuleType());
        dto.setWeight(br.getRule().getWeight());
        dto.setRuleCode(br.getRule().getRuleCode());
        dto.setFalsePositive(br.getFalsePositive());
        return dto;
    }
}
