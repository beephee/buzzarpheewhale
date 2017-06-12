package com.example.android.firebaseauthdemo;

public class User {

    String userUID;
    String userEmail;
    String userType;
    String blacklisted;
    String tutorial;
    String custsvc;

    public User(){

    }

    public User(String userUID, String userEmail, String userType, String blacklisted, String tutorial, String custsvc) {
        this.userUID = userUID;
        this.userEmail = userEmail;
        this.userType = userType;
        this.blacklisted = blacklisted;
        this.tutorial = tutorial;
        this.custsvc = custsvc;
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

}
