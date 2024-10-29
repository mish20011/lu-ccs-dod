package com.catignascabela.dodapplication;

public class SummaryItem {
    private String offenseCategory;
    private String description;
    private String possibleSanctions;

    public SummaryItem(String offenseCategory, String description, String possibleSanctions) {
        this.offenseCategory = offenseCategory;
        this.description = description;
        this.possibleSanctions = possibleSanctions;
    }

    public String getOffenseCategory() {
        return offenseCategory;
    }

    public String getDescription() {
        return description;
    }

    public String getPossibleSanctions() {
        return possibleSanctions;
    }
}
