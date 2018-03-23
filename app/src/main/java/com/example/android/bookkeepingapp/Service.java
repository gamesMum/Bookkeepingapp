package com.example.android.bookkeepingapp;

/**
 * The service that will be included in each invoice
 */

public class Service {
    private String serviceId;
    private String serviceName;
    private double servicePrice;

    Service(String serviceId, String serviceName, double servicePrice)
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public double getServicePrice()
    {
        return servicePrice;
    }

    public void setNewService(String serviceName, double servicePrice )
    {
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

}
