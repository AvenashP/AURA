package com.avenashp.auratest.ModelClass;

public class ChatModel {
    String message, date, time, sender,morse_code;

    public ChatModel(){

    }

    public ChatModel(String message, String time, String sender, String date,String morse_code) {
        this.message = message;
        this.time = time;
        this.sender = sender;
        this.date = date;
        this.morse_code = morse_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMorse_code() {
        return morse_code;
    }

    public void setMorse_code(String morse_code) {
        this.morse_code = morse_code;
    }
}
