package com.example.android.bookkeepingapp;

/**
 * Created by Rasha on 19/03/2018.
 */

public class Services {

    private String serviceName;
    private double servicePrice;

    Services(String serviceName, double servicePrice)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public double getServicePrice()
    {
        return servicePrice;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public void setServicePrice(double servicePrice)
    {
        this.servicePrice = servicePrice;
    }
}
