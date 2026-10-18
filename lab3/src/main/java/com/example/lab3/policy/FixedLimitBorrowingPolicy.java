package com.example.lab3.policy;

import com.example.lab3.model.Member;

public class FixedLimitBorrowingPolicy implements BorrowingPolicy {
    private final int limit;

    public FixedLimitBorrowingPolicy(int limit) {
        this.limit = limit;
    }

    @Override
    public int borrowingLimit(Member member) {
        return limit;
    }
}
