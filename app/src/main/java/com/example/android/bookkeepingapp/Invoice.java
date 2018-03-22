package com.example.android.bookkeepingapp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * Invoice class
 */

public class Invoice {
    private int clientID;
    private List<Integer> serviceID;

    private static long invoiceNumber = 0;
    private String issueDate;
    private String dueDate;
    private double total;
    private final boolean NOT_PAID = false;
    private boolean invoiceStatus = NOT_PAID;

    public static double profit;

    Invoice(int clientID, List<Integer> serviceID,
            String dueDate)
    {

        this.clientID = clientID;
        this.serviceID = serviceID;
        this.dueDate = dueDate;
        this.issueDate = getCurrentDate();
        this.invoiceNumber++;
        invoiceStatus = NOT_PAID;

    }

    public boolean isPaid() {
        return true;
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
}
