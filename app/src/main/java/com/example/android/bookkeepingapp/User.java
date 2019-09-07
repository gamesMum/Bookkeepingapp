package com.example.android.bookkeepingapp;

import java.text.DecimalFormat;

/**
 * Created by Rasha on 02/05/2018.
 */

public class User {
    private String userId;
    private String username;
    private String userEmail;
    private double totalProfit;
    private double totalExpenses;

    public User()
    {

    }
    public User(String userId, String userEmail , double totalProfit, double totalExpenses) {
        this.userId = userId;
        this.totalExpenses = roundTwoDecimals(totalExpenses);
        this.totalProfit = roundTwoDecimals(totalProfit);
        this.userEmail = userEmail;
    }
    public User(String userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }


    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = roundTwoDecimals(totalProfit);
    }
    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm;
        twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
