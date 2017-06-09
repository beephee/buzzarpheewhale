package com.example.android.firebaseauthdemo;

public class User {

    String userUID;
    String userEmail;
    String userType;
    String blacklisted;

    public User(){

    }

    public User(String userUID, String userEmail, String userType, String blacklisted) {
        this.userUID = userUID;
        this.userEmail = userEmail;
        this.userType = userType;
        this.blacklisted = blacklisted;
    }

    public String getuserUID() {
        return userUID;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public String getuserType() { return userType; }

    public String getBlacklisted() { return blacklisted; }

}
