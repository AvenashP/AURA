package com.avenashp.auratest.ModelClass;

public class UserModel {

    private String name,age,gender,number;

    public UserModel(String name, String age, String gender, String number) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
