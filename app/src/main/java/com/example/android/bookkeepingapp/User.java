package com.example.android.bookkeepingapp;

/**
 * Created by Rasha on 02/05/2018.
 */

public class User {
    private String userId;
    private String username;
    private String userEmail;
    private double profit;

    public User(String userId, String userEmail , double profit) {
        this.userId = userId;
        this.profit = profit;
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getProfit() {
        return profit;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
