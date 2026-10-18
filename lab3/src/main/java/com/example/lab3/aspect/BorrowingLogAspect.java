package com.example.lab3.aspect;

import com.example.lab3.model.Book;
import com.example.lab3.model.Member;
import com.example.lab3.service.BorrowingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Order(2)
public class BorrowingLogAspect {
    private final BorrowingService borrowingService;

    public BorrowingLogAspect(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @Around(
            "execution(* com.example.lab3.service.BorrowingService.borrowBook(..))"
                    + " || execution(* com.example.lab3.service.BorrowingService.returnBook(..))"
    )
    public Object logBorrowOrReturn(ProceedingJoinPoint joinPoint)
            throws Throwable {

        Object[] args = joinPoint.getArgs();
        UUID memberId = (UUID) args[0];
        UUID bookId = (UUID) args[1];

        Member member = borrowingService.findMember(memberId);
        Book book = borrowingService.findBook(bookId);

        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;

            System.out.println(
                    "LOG: method=" + joinPoint.getSignature().getName()
                            + ", member=" + member.getName()
                            + ", book=" + book.getTitle()
                            + ", executionTime=" + elapsedTime + "ms"
            );
        }
    }
}
