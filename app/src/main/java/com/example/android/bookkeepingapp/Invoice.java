package com.example.android.bookkeepingapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Information stored in each invoice6
 */

public class Invoice {
    private static long invoiceNumber;

    private ArrayList<String> orderNums;
    private ArrayList<String> paymentNums;
    private String clientID;
    private String issueDate;
    private String dueDate;

    private double total;
    private double amountLeft;


    //the invoice is either paid, not paid, or partially paid
    int paid;
    private final int NOT_PAID = 0;
    private final int PAID = 1;
    private final int PARTIALLY_PAID = -1;

    private final String NO_PAYMENTS = "n/y";


    Invoice(String clientID, ArrayList<String> orderNum)
    {
        int count = 0;
        this.clientID = clientID;
        //by default the due date is after 15 days
        this.dueDate = setDueDateAfter( getIssueDate(), 15 );
        //get the current system date
        this.issueDate = getCurrentDate();

        //pass the services we will provide in this invoice
        this.orderNums = orderNum;
        this.paid = NOT_PAID;
        this.paymentNums.add(NO_PAYMENTS);

        //start with Id number 1
        this.invoiceNumber = count++; //increase the Id with each object

    }

    //*********Getters************************************************//
    public static long getInvoiceNumber() {
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

    //give it the services to calculate the total
    public double getInvoiceTotal(ArrayList<Order> orders) {
        double invoiceTotal = 0;
        //the sum of all the services for this invoice
        for(Order order : orders) {
           //invoiceTotal += order.getTotal();
        }
        return invoiceTotal;
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

    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
    //****************************************************************//


//*******************Setters****************************************************************//


    /**
     * Get the system date (using it for the issue date)
     * @return
     */

    //get the date after the desired number of days
    //this is the Due date for the invoice
    public String setDueDateAfter(String date, int days)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(issueDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //add the number of days for the invoice over due
        c.add(Calendar.DAY_OF_MONTH, days);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy / MM / dd ");
        //get the date in the correct format
        date = sdf1.format(c.getTime());

        return date;
    }

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
