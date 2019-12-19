package com.avenashp.auratest.ModelClass;

public class ContactModel {

    private String short_name, long_name, number, chat_id;

    public ContactModel() {

    }

    public ContactModel(String short_name, String long_name, String number, String chat_id) {
        this.short_name = short_name;
        this.long_name = long_name;
        this.number = number;
        this.chat_id = chat_id;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getLong_name() {
        return long_name;
    }

    public String getNumber() {
        return number;
    }

    public String getChat_id() {
        return chat_id;
    }
}
