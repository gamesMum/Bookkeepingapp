package com.example.android.bookkeepingapp;

import java.text.DecimalFormat;

public class Expense {
    private String expenseNum;
    private String expenseName;
    private double expenseValue;
    private String expenseNote;
    private String issueDate;

    Expense()
    {}

    Expense(String expenseNum, String expenseName, double expenseValue, String issueDate)
    {
        this.expenseNum = expenseNum;
        this.expenseName = expenseName;
        this.expenseValue = roundTwoDecimals(expenseValue);
        this.issueDate = issueDate;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public void setExpenseValue(double expenseValue) {
        this.expenseValue = roundTwoDecimals(expenseValue);
    }

    public void setExpenseNum(String expenseNum) {
        this.expenseNum = expenseNum;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setExpenseNote(String expenseNote) {
        this.expenseNote = expenseNote;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getExpenseValue() {
        return expenseValue;
    }

    public String getExpenseNote() {
        return expenseNote;
    }

    public String getExpenseNum() {
        return expenseNum;
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public String getIssueDate() {
        return issueDate;
    }
}


