package com.example.android.bookkeepingapp;

/**
 * Each Invoice may include multiple payments
 * which will include the amount and the date of payment
 */

public class Payment {
    private String paymentId;
    private double amount;
    private String payDate;

    Payment(double amount, String payDate)
    {
        this.amount = amount;
        this.payDate = payDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getPayDate() {
        return payDate;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
