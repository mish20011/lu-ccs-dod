package com.catignascabela.dodapplication;

public class Violation {
    private String violation;
    private String punishment;

    public Violation(String violation, String punishment) {
        this.violation = violation;
        this.punishment = punishment;
    }

    public String getViolation() {
        return violation;
    }

    public String getPunishment() {
        return punishment;
    }
}
