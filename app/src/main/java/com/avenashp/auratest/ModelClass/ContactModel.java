package com.avenashp.auratest.ModelClass;

public class ContactModel {

    private String shortName, longName, mobileNumber, chatId;

    public ContactModel(String shortName, String longName, String mobileNumber, String chatId) {
        this.shortName = shortName;
        this.longName = longName;
        this.mobileNumber = mobileNumber;
        this.chatId = chatId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
