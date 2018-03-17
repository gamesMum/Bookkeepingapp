package com.example.android.bookkeepingapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the main information for each customer
 */

public class Client {

    //client information
    private static long clientId = 0;
    private String firstName;
    private String lastName;

    private String companyName;

    private String address;
    private String email;
    private String phoneNumber;

    public Client()
    {}

    public Client(String firstName, String lastName)
    {
        //take the last value and increase it by one
        clientId = clientId + 1;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Client(String companyName)
    {
        this.companyName = companyName;
    }

    public String getmAddress() {
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

    public static long getClientId() {
        return clientId;
    }

    public String getmPhoneNumber() {
        return this.phoneNumber;
    }

    public void setmAddress(String address) {
        this.address = address;
    }

    public void setmCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setmEmail(String email) {
        this.email = email;
    }

    public void setmFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setmLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setmPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
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
