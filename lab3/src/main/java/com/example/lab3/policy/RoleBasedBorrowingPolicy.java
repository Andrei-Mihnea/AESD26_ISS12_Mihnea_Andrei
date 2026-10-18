package com.example.lab3.policy;

import com.example.lab3.model.Member;
import com.example.lab3.model.MemberRole;

public class RoleBasedBorrowingPolicy implements BorrowingPolicy {

    @Override
    public int borrowingLimit(Member member) {
        if (member.getRole() == MemberRole.STUDENT) {
            return 2;
        }

        if (member.getRole() == MemberRole.REGULAR) {
            return 3;
        }

        if (member.getRole() == MemberRole.PREMIUM) {
            return 5;
        }

        throw new IllegalArgumentException("Unknown member role");
    }
}