package com.example.android.bookkeepingapp;

/**
 * represent one order/service to be collected in the invoice
 */

public class Order {
    private String clientId;
    private String orderNum;//firebase order Id
    private String serviceNum;
    private int quantity;

    Order(String orderNum, String clientId, String serviceNum, int quantity)
    {
        this.orderNum = orderNum;
        this.serviceNum = serviceNum;
        this.quantity = quantity;
        this.clientId = clientId;
    }

    Order(String orderNum, String clientId, String serviceNum)
    {
        this.orderNum = orderNum;
        this.serviceNum = serviceNum;
        this.quantity = 1;
        this.clientId = clientId;
    }

    /**use this to get the service price, name
     *
     * @return
     */
    public String getServiceNum() {
        return serviceNum;
    }

    /**get the order number to add its information to the invoice
     *
     * @return
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * What is the quantity for this specific service
     * @return
     */
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Order{" + "clientId='" + clientId + '\'' +
                ", orderNum='" + orderNum + '\'' + ", serviceNum='" +
                serviceNum + '\'' + ", quantity=" + quantity + '}';
    }
}

