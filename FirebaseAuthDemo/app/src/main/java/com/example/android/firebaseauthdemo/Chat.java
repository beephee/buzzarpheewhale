package com.example.android.firebaseauthdemo;

public class Chat {

    String name;
    String msg;
    String date;
    String time;
    String uid;

    public Chat(){

    }

    public Chat(String name, String msg, String date, String time, String uid) {
        this.name = name;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getUid() { return uid; }

}
