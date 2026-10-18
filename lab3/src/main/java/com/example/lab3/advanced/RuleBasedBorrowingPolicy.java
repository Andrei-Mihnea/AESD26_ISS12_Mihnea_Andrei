package com.example.lab3.advanced;

import com.example.lab3.model.Book;
import com.example.lab3.model.Member;
import com.example.lab3.model.MemberRole;
import com.example.lab3.policy.BorrowingDecision;
import com.example.lab3.policy.BorrowingPolicy;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "library.borrowing-policy", havingValue = "rules")
public class RuleBasedBorrowingPolicy implements BorrowingPolicy {
    private final KieContainer kieContainer;

    public RuleBasedBorrowingPolicy(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @Override
    public int borrowingLimit(Member member) {
        return switch (member.getRole()) {
            case STUDENT -> 2;
            case REGULAR -> 3;
            case PREMIUM -> 5;
        };
    }

    @Override
    public BorrowingDecision evaluate(Member member, Book book) {
        BorrowingRequest request = new BorrowingRequest(
                member.getRole(),
                member.getBorrowedBooks().size(),
                book.getType(),
                book.isAvailable()
        );

        KieSession session = kieContainer.newKieSession("borrowingKSession");
        try {
            session.insert(request);
            session.fireAllRules();
        } finally {
            session.dispose();
        }

        BorrowingDecision decision = request.getDecision();
        if (!decision.isDecided()) {
            return BorrowingDecision.denied(
                    borrowingLimit(member), "No borrowing rule matched this request"
            );
        }
        return decision;
    }
}
