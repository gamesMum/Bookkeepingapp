package com.example.android.bookkeepingapp;

/**
 * The service that will be included in each invoice
 */

public class Service {
    private String serviceNum;
    private String serviceName;
    private double servicePrice;
    private String serviceNotes;


    Service()
    {}
    Service(String serviceNum, String serviceName, double servicePrice)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceNum = serviceNum;
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
