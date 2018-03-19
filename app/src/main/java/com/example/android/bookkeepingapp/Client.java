package com.example.android.bookkeepingapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the main information for each customer
 */

public class Client {

    //client information
    private String clientId;
    private String firstName;
    private String lastName;

    private String companyName;

    private String address;
    private String email;
    private String phoneNumber;

    Client()
    {}

    Client(String clientId, String firstName, String lastName)
    {
        //take the last value and increase it by one
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    Client(String companyName)
    {
        this.companyName = companyName;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    /**
     * Map will use for writing the items to the database.
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        /*result.put("companyName" ,companyName);
        result.put("address" ,address);
        result.put("email" ,email);
        result.put("phoneNumber" ,phoneNumber);*/
        return result;
    }
}
