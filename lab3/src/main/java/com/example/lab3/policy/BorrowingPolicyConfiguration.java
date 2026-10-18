package com.example.lab3.policy;

import com.example.lab3.advanced.RuleBasedBorrowingPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BorrowingPolicyConfiguration {

    @Bean
    @ConditionalOnProperty(name = "library.borrowing-policy", havingValue = "fixed", matchIfMissing = true)
    public BorrowingPolicy fixedBorrowingPolicy(
            @Value("${library.fixed-limit:3}") int fixedLimit) {
        return new FixedLimitBorrowingPolicy(fixedLimit);
    }

    @Bean
    @ConditionalOnProperty(name = "library.borrowing-policy", havingValue = "role-based")
    public BorrowingPolicy roleBasedBorrowingPolicy() {
        return new RoleBasedBorrowingPolicy();
    }

    @Bean
    @ConditionalOnProperty(name = "library.borrowing-policy", havingValue = "rules")
    public BorrowingPolicy ruleBasedBorrowingPolicy(
            RuleBasedBorrowingPolicy policy) {
        return policy;
    }
}
