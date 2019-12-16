package com.avenashp.auratest.ModelClass;

public class ChatModel {
    String userid,message,date,time;

    public ChatModel(String userid, String message, String date, String time) {
        this.userid = userid;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
