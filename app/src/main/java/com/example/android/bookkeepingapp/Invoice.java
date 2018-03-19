package com.example.android.bookkeepingapp;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Rasha on 18/03/2018.
 */

public class Invoice {
    private int clientID;
    private List<Integer> serviceID;

    private static long invoiceNumber = 0;
    private Date issueDate;
    private Date dueDate;
    private double total;
    private final boolean NOT_PAID = false;
    private boolean invoiceStatus = NOT_PAID;

    public static double profit;

    Invoice(int clientID, List<Integer> serviceID,
            Date dueDate)
    {

        this.clientID = clientID;
        this.serviceID = serviceID;
        this.dueDate = dueDate;
        this.issueDate = Calendar.getInstance().getTime();
        this.invoiceNumber++;
        invoiceStatus = NOT_PAID;

    }

    public boolean isPaid() {
        return true;
    }


}
