package com.example.lab3.policy;

public class BorrowingDecision {
    private boolean decided;
    private boolean allowed;
    private int borrowingLimit;
    private int borrowingPeriodDays;
    private String reason;

    public boolean isDecided() { return decided; }
    public void setDecided(boolean decided) { this.decided = decided; }
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
    public int getBorrowingLimit() { return borrowingLimit; }
    public void setBorrowingLimit(int borrowingLimit) { this.borrowingLimit = borrowingLimit; }
    public int getBorrowingPeriodDays() { return borrowingPeriodDays; }
    public void setBorrowingPeriodDays(int borrowingPeriodDays) { this.borrowingPeriodDays = borrowingPeriodDays; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public static BorrowingDecision denied(int limit, String reason) {
        BorrowingDecision decision = new BorrowingDecision();
        decision.decided = true;
        decision.allowed = false;
        decision.borrowingLimit = limit;
        decision.reason = reason;
        return decision;
    }

    public static BorrowingDecision allowed(int limit, int periodDays) {
        BorrowingDecision decision = new BorrowingDecision();
        decision.decided = true;
        decision.allowed = true;
        decision.borrowingLimit = limit;
        decision.borrowingPeriodDays = periodDays;
        return decision;
    }
}
