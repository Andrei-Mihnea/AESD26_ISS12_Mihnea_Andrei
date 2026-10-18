package com.example.lab3.advanced;

import com.example.lab3.model.BookType;
import com.example.lab3.model.MemberRole;
import com.example.lab3.policy.BorrowingDecision;

public class BorrowingRequest {
    private final MemberRole memberRole;
    private final int borrowedCount;
    private final BookType bookType;
    private final boolean bookAvailable;
    private final BorrowingDecision decision = new BorrowingDecision();

    public BorrowingRequest(MemberRole memberRole, int borrowedCount,
                            BookType bookType, boolean bookAvailable) {
        this.memberRole = memberRole;
        this.borrowedCount = borrowedCount;
        this.bookType = bookType;
        this.bookAvailable = bookAvailable;
    }

    public MemberRole getMemberRole() { return memberRole; }
    public int getBorrowedCount() { return borrowedCount; }
    public BookType getBookType() { return bookType; }
    public boolean isBookAvailable() { return bookAvailable; }
    public BorrowingDecision getDecision() { return decision; }
}
