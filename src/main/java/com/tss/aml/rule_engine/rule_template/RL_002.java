package com.tss.aml.rule_engine.rule_template;

import com.tss.aml.model.ParameterMeta;
import com.tss.aml.tenant.entity.Customer;
import com.tss.aml.tenant.entity.Transaction;
import com.tss.aml.tenant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RL_002 implements CustomerRuleTemplate {

    private final TransactionRepository transactionRepository;

    @Override
    public List<Transaction> check(Customer customer, Map<String, Object> parameters) {

        int durationDays = (int) parameters.get("duration");
        double threshold = (double) parameters.get("threshold");

        LocalDateTime fromTime = LocalDateTime.now().minusDays(durationDays);

        long count = transactionRepository.countByCustomerAndTxnTimeAfter(
                customer,
                fromTime
        );

        if(count <= threshold){
            return null;
        }

        return getViolatingTransactions(customer, fromTime);
    }

    private List<Transaction> getViolatingTransactions(Customer customer, LocalDateTime fromTime) {
        if (customer == null || fromTime == null) {
            return List.of();
        }

        return transactionRepository.findByCustomerAndTxnTimeAfter(
                customer,
                fromTime
        );
    }

    @Override
    public Map<String, ParameterMeta> getRequiredParameters() {
        return Map.of(
                "duration", new ParameterMeta(Integer.class, true, true),
                "threshold", new ParameterMeta(Double.class, true, true)
        );
    }

    @Override
    public String getDescription() {
        return "Per [Duration] Day/s Transaction Frequency [Greater] than [threshold].";
    }
}