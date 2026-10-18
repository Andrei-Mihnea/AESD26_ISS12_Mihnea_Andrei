package com.example.lab3.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberBorrowingLimitListener {
    @EventListener
    public void onMemberBorrowingLimitReached(MemberBorrowingLimitEvent event) {
        System.out.println(
                "EVENT: " + event.getMemberName()
                        + " now has " + event.getBorrowedCount()
                        + " borrowed books."
        );
    }
}