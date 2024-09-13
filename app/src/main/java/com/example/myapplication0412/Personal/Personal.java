package com.example.myapplication0412.Personal;

public class Personal {
    private String name;
    private String date;
    private String gender;
    private String age;
    private String additionalInfo1;
    private String additionalInfo2;
    private String address;

    // No-argument constructor required for Firebase
    public Personal() {
    }

    public Personal(String name, String date, String gender, String age, String additionalInfo1, String additionalInfo2 , String address) {
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.age = age;
        this.additionalInfo1 = additionalInfo1;
        this.additionalInfo2 = additionalInfo2;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAdditionalInfo1() {
        return additionalInfo1;
    }

    public void setAdditionalInfo1(String additionalInfo1) {
        this.additionalInfo1 = additionalInfo1;
    }

    public String getAdditionalInfo2() {
        return additionalInfo2;
    }

    public void setAdditionalInfo2(String additionalInfo2) {
        this.additionalInfo2 = additionalInfo2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
