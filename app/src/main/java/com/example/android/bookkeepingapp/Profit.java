package com.example.android.bookkeepingapp;

import java.text.DecimalFormat;

public class Profit {
    private String profitNum;
    private String profitName;
    private double profitValue;
    private String profitNote;
    private String issueDate;

    Profit()
    {}

    Profit(String profitNum, String profitName, double profitValue, String issueDate)
    {
        this.profitNum = profitNum;
        this.profitName = profitName;
        this.profitValue = roundTwoDecimals(profitValue);
        this.issueDate = issueDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public double getProfitValue() {
        return profitValue;
    }

    public String getProfitName() {
        return profitName;
    }

    public String getProfitNote() {
        return profitNote;
    }

    public String getProfitNum() {
        return profitNum;
    }

    public void setProfitName(String profitName) {
        this.profitName = profitName;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setProfitNum(String profitNum) {
        this.profitNum = profitNum;
    }

    public void setProfitNote(String profitNote) {
        this.profitNote = profitNote;
    }

    public void setProvitValue(double provitValue) {
        this.profitValue = provitValue;
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

}
