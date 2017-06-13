package com.example.android.firebaseauthdemo;

public class User {

    String userUID;
    String userEmail;
    String userType;
    String blacklisted;
    String tutorial;
    String custsvc;
    Boolean courierActive;
    String buyerCountry;
    String courierCountry;
    String maxWeight;
    String dateDeparture;
    String buyerBudget; //KIV if want to implement in the future

    public User(){

    }

    public User(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc, Boolean courierActive, String buyerCountry, String courierCountry, String maxWeight, String dateDeparture, String buyerBudget) {
        this.userUID = userUID;
        this.userEmail = userEmail;
        this.userType = userType;
        this.blacklisted = blacklisted;
        this.tutorial = tutorial;
        this.custsvc = custsvc;
        this.courierActive = courierActive;
        this.buyerCountry = buyerCountry;
        this.courierCountry = courierCountry;
        this.maxWeight = maxWeight;
        this.dateDeparture = dateDeparture;
        this.buyerBudget = buyerBudget;
    }

    public String getuserUID() {
        return userUID;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public String getuserType() { return userType; }

    public String getBlacklisted() { return blacklisted; }

    public String getTutorial() { return tutorial; }

    public String getCustsvc() { return custsvc; }

    public Boolean getCourierActive() { return courierActive; }

    public String getBuyerCountry() { return buyerCountry; }

    public String getCourierCountry() { return courierCountry; }

    public String getMaxWeight() { return maxWeight; }

    public String getDateDeparture() { return dateDeparture; }

    public String getBuyerBudget() { return buyerBudget; }

}
