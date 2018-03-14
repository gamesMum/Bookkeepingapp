package com.example.android.bookkeepingapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the main information for each customer
 */

public class Client {

    //client information
    private String mFirstName;
    private String mLastName;

    private String mCompanyName;

    private String mAddress;
    private String mEmail;
    private String mPhoneNumber;


    public Client(String firstName, String mLastName)
    {
        mFirstName = firstName;
        mLastName = getmLastName();
    }

    public String getmAddress() {
        return mAddress;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mCompanyName='" + mCompanyName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                '}';
    }

    /**
     * Map will use for writing the items to the database.
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", mFirstName);
        result.put("lastName", mLastName);
        result.put("companyName" ,mCompanyName);
        result.put("address" ,mAddress);
        result.put("email" ,mEmail);
        result.put("phoneNumber" ,mPhoneNumber);
        return result;
    }
}
