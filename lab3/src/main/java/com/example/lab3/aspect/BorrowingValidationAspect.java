package com.example.lab3.aspect;

import com.example.lab3.exception.BorrowingException;
import com.example.lab3.model.Book;
import com.example.lab3.model.Member;
import com.example.lab3.service.BorrowingService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Order(1)
public class BorrowingValidationAspect {
    private final BorrowingService borrowingService;

    public BorrowingValidationAspect(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @Before(
            "execution(* com.example.lab3.service.BorrowingService.borrowBook(..))"
                    + " && args(memberId, bookId)"
    )
    public void validateBorrow(UUID memberId, UUID bookId) {
        Member member = borrowingService.findMember(memberId);
        Book book = borrowingService.findBook(bookId);

        var decision = borrowingService.getBorrowingPolicy().evaluate(member, book);
        if (!decision.isAllowed()) {
            throw new BorrowingException(decision.getReason());
        }
        System.out.println("Borrow approved for "
                + decision.getBorrowingPeriodDays() + " days");
    }
}
