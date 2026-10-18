package com.example.lab3.aspect;

import com.example.lab3.exception.BorrowingException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(3)
public class BorrowingExceptionAspect {

    @AfterThrowing(
            pointcut =
                    "execution(* com.example.lab3.service.BorrowingService.borrowBook(..))"
                            + " || execution(* com.example.lab3.service.BorrowingService.returnBook(..))",
            throwing = "exception"
    )
    public void logBorrowingError(BorrowingException exception) {
        System.err.println(
                "ERROR: borrowing operation failed: " + exception.getMessage()
        );
    }
}