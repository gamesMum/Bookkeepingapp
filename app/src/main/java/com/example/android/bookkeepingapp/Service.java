package com.example.android.bookkeepingapp;

/**
 * The service that will be included in each invoice
 */

public class Service {
    private String serviceNum;
    private String serviceName;
    private double servicePrice;
    private double serviceProfitRate;
    private double servicePlusProfit;
    private double servicePriceIQ;
    private String serviceNotes;

    //Add image here (optional)



    Service()
    {}
    Service(String serviceNum, String serviceName, double servicePrice, double serviceProfitRate)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceNum = serviceNum;
        this.serviceProfitRate = serviceProfitRate;
    }

    public double getServiceProfitRate() {
        return serviceProfitRate;
    }

    public void setServiceProfitRate(double serviceProfitRate) {
        this.serviceProfitRate = serviceProfitRate;
    }

    public void setServicePriceIQ(double servicePriceIQ) {
        this.servicePriceIQ = servicePriceIQ;
    }

    public double getServicePriceIQ() {
        return servicePriceIQ;
    }

    public double getServicePlusProfit() {
        return servicePlusProfit;
    }

    public void setServicePlusProfit(double servicePlusProfit) {
        this.servicePlusProfit = servicePlusProfit;
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
}
