package com.example.android.firebaseauthdemo;

public class Chat {

    String name;
    String msg;
    String date;
    String time;

    public Chat(){

    }

    public Chat(String name, String msg, String date, String time) {
        this.name = name;
        this.msg = msg;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() { return date; }

    public String getTime() { return time; }

}
