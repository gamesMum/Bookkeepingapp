package com.example.android.bookkeepingapp;

/**
 * The service that will be included in each invoice
 */

public class Service {
    private String serviceNum;
    private String serviceName;
    private double servicePrice;


    Service(String serviceId, String serviceName, double servicePrice)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceNum = serviceId;
    }

    public String getServiceId() {
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

}
