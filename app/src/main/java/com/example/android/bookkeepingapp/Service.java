package com.example.android.bookkeepingapp;

import java.text.DecimalFormat;

/**
 * The service that will be included in each invoice
 */

public class Service {
    private String serviceNum;
    private String serviceName;
    private double servicePrice;
    private double servicePlusProfit;
    private double servicePriceSecCurrency;
    private String serviceNotes;

    //Add image here (optional)



    Service()
    {}
    Service(String serviceNum, String serviceName, double servicePrice,
            double servicePriceSecCurrency)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceNum = serviceNum;
        this.servicePriceSecCurrency = servicePriceSecCurrency;
    }


    public void setServicePriceSecCurrency(double servicePriceSecCurrency) {
        this.servicePriceSecCurrency = servicePriceSecCurrency;
    }

    public double getServicePriceSecCurrency() {
        return servicePriceSecCurrency;
    }

    public double getServicePlusProfit() {
        return servicePlusProfit;
    }

    public void setServicePlusProfit(double servicePlusProfit) {
        this.servicePlusProfit = roundTwoDecimals( servicePlusProfit );
    }

    public String getServiceNum() {
        return serviceNum;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public double getServicePrice()
    {
        return servicePrice;
    }

    public String getServiceNotes() {
        return serviceNotes;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePrice(double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public void setServiceNotes(String serviceNotes) {
        this.serviceNotes = serviceNotes;
    }

    public void setServiceNum(String serviceNum) {
        this.serviceNum = serviceNum;
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
