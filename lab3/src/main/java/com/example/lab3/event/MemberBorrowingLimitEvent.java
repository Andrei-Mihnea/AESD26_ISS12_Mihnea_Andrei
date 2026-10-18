package com.example.lab3.event;

public class MemberBorrowingLimitEvent {
    private final String memberName;
    private final int borrowedCount;

    public MemberBorrowingLimitEvent(String memberName, int borrowedCount) {
        this.memberName = memberName;
        this.borrowedCount = borrowedCount;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }
}