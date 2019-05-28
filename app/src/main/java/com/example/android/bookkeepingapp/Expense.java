package com.example.android.bookkeepingapp;

public class Expense {
    private String expenseNum;
    private String expenseName;
    private double expenseValue;
    private String expenseNote;

    Expense()
    {}

    Expense(String expenseNum, String expenseName, double expenseValue)
    {
        this.expenseNum = expenseNum;
        this.expenseName = expenseName;
        this.expenseValue = expenseValue;
    }

    public void setExpenseNum(String expenseNum) {
        this.expenseNum = expenseNum;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public void setExpenseValue(double expenseValue) {
        this.expenseValue = expenseValue;
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
}
