package com.example.android.bookkeepingapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Information stored in each invoice6
 */

public class Invoice {

    private String invoiceNumber;
    private ArrayList<String> orderNums;
    private ArrayList<String> paymentNums;
    private String clientID;
    private String issueDate;
    private String dueDate;
    private double invoiceProfit;
    private double invoiceExpenses;
    private double total;
    private double amountLeft;


    //the invoice is either paid, not paid, or partially paid
    int paid;
    private final int NOT_PAID = 0;
    private final int PAID = 1;
    private final int PARTIALLY_PAID = -1;

    private final String NO_PAYMENTS = "n/y";

    Invoice()
    {

    }
    Invoice(String invoiceNumber, String clientID, ArrayList<String> orderNum,
            String issueDate, String dueDate, double total, double profit, double expenses)
    {
        this.invoiceNumber = invoiceNumber;
        this.clientID = clientID;
        this.dueDate = dueDate;
        this.issueDate = issueDate;
        this.invoiceExpenses = expenses;
        this.invoiceProfit = profit;
        this.total = total;

        //pass the services we will provide in this invoice
        this.orderNums = orderNum;
        this.paid = NOT_PAID;
        //this.paymentNums.add(NO_PAYMENTS);



    }

    //*********Getters************************************************//
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getClientID() {
        return clientID;
    }

    public ArrayList<String> getOrderNums() {
        return orderNums;
    }

    public ArrayList<String> getPaymentNums() {
        return paymentNums;
    }

    public double getTotal() {
        return total;
    }

    public double getInvoiceProfit(){return  invoiceProfit; }

    public double getInvoiceExpenses() {
        return invoiceExpenses;
    }

    public int getIsPaid() {
        return paid;
    }

    public int getInvoiceStatus()
    {
        if(paid == NOT_PAID)
            return 0;
        else if(paid == PARTIALLY_PAID)
            return -1;
        else
            return 1;//IS PAID
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setOrderNums(ArrayList<String> orderNums) {
        this.orderNums = orderNums;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setInvoiceProfit(double invoiceProfit) {
        this.invoiceProfit = invoiceProfit;
    }

    public void setInvoiceExpenses(double invoiceExpenses) {
        this.invoiceExpenses = invoiceExpenses;
    }

    //****************************************************************//


//*******************Setters****************************************************************//


    /**
     * Get the system date (using it for the issue date)
     * @return
     */

    /**
     *
     * @param paymentAmount
     * @param currentDate
     * @param invoiceTotal
     * @return
     */
   /* public double setPaymentGetAmountLeft(double paymentAmount, String currentDate, double invoiceTotal) {

        double invoiceAmountLeft = invoiceTotal;
        if(paymentAmount == invoiceTotal)
        {
            //th invoice is paid
            paid = PAID;
            invoiceAmountLeft = 0;
            //create new payment instance and store the date and the amount
            Payment payment = new Payment( paymentAmount, currentDate );
            //add it to the payments array list of Ids
            this.paymentIds.add(payment.getPaymentId());
            return invoiceAmountLeft;

        }else if(paymentAmount < invoiceTotal)
        {

            //th invoice ia partially paid
            paid = PARTIALLY_PAID;
            //the amount left to pay minus this payment
            invoiceAmountLeft = invoiceAmountLeft - paymentAmount;
            //create new payment instance and store the date and the amount
            Payment payment = new Payment( paymentAmount, currentDate );
            //add it to the payments array list of Ids
            this.paymentIds.add(payment.getPaymentId());
            //return the left amount
            return invoiceAmountLeft;
        }
        return invoiceAmountLeft;

    }*/
//******************************************************************************************//


}
