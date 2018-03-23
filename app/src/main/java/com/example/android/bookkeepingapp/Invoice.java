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

    private ArrayList<Service> services;
    private ArrayList<Payment> payments;
    private int clientID;
    private String issueDate;
    private String dueDate;
    private double InvoiceTotal;
    //the amount left after a payment
    private double InvoiceAmountLeft;
    //the invoice is either paid, not paid, or partially paid
    int paid;
    private final int NOT_PAID = 0;
    private final int PAID = 1;
    private final int PARTIALLY_PAID = -1;


    Invoice(int clientID, ArrayList<Service> services)
    {
        int count = 0;
        this.InvoiceTotal = getInvoiceTotal( );
        this.InvoiceAmountLeft = this.InvoiceTotal;
        this.clientID = clientID;
        //at default the due date is after 15 days
        this.dueDate = getDateAfter( 15 );
        //get the current system date
        this.issueDate = getCurrentDate();

        //pass the services we will provide in this invoice
        this.services = services;
        this.paid = NOT_PAID;
        this.payments.add(null);

        //start with Id number 1
        this.invoiceNumber = count++; //increase the Id with each object

    }

    /**
     * Get the system date (using it for the issue date)
     * @return
     */
    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    //get the date after the desired number of days
    //this is the Due date for the invoice
    private String getDateAfter(int days)
    {
        //get the issue date for the invoice
        String issueDate = this.issueDate;
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
        String output = sdf1.format(c.getTime());

        return output;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public static long getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getClientID() {
        return clientID;
    }

    public double getInvoiceTotal() {
        //the sum of all the services for this invoice
        for(Service service : services) {
            this.InvoiceTotal += service.getServicePrice();
        }
        return InvoiceTotal;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public int getInvoiceStatus()
    {
        if(paid == NOT_PAID)
            return 0;
        else if(paid == PARTIALLY_PAID)
            return -1;
        else
            return 1;
    }

    /**
     * get all the payments for specific invoice
     * @return
     */
    public ArrayList<Payment> getPayment() {
        return payments;
    }

    public void setPayment(double payment, String currentDate) {
        if(payment == InvoiceAmountLeft)
        {
            //th invoice is paid
            paid = PAID;
            //add the payment to the array list and its date
            payments.add(new Payment( payment, currentDate ));

        }else if(payment < InvoiceAmountLeft)
        {
            //th invoice ia partially paid
            paid = PARTIALLY_PAID;
            //the amount left to pay minus this payment
            InvoiceAmountLeft = InvoiceAmountLeft - payment;
            //add the payment to the array list
            payments.add(new Payment( payment, currentDate ));
        }else //the payment is more
        return; //exit

    }


    public void setDueDateAfter(int days) {
        this.dueDate = getDateAfter( days );
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

}
