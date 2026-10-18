package com.example.lab3.policy;

import com.example.lab3.model.Member;
import com.example.lab3.model.Book;

public interface BorrowingPolicy {
    int borrowingLimit(Member member);

    default BorrowingDecision evaluate(Member member, Book book) {
        int limit = borrowingLimit(member);
        if (!book.isAvailable()) {
            return BorrowingDecision.denied(limit, "Book is not available");
        }
        if (member.getBorrowedBooks().size() >= limit) {
            return BorrowingDecision.denied(limit, "Member reached the borrowing limit");
        }
        return BorrowingDecision.allowed(limit, 14);
    }
}
