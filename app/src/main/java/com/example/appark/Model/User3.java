package com.example.appark.Model;

import java.sql.Struct;

public class User3 {

    private String vehicle;
    private String city;
    private String email;
    private String lastLoginDate;
    private String lastLoginTime;
    private String name;
    private String contact;

    public String locname;
    public double latitude;
    public double longitude;
    public double price;
    public double nslot;

    public User3(String vehicle, String city, String email, String lastLoginDate, String lastLoginTime, String name, String contact,
                 String locname, double latitude, double longitude, double price, double nslot) {
        this.vehicle = vehicle;
        this.city = city;
        this.email = email;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginTime = lastLoginTime;
        this.name = name;
        this.contact = contact;
        this.locname = locname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.nslot = nslot;
    }

    public User3(){

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getlocname() {
        return locname;
    }


    public String getvehicle() {
        return vehicle;
    }

    public void setvehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getcontact() {
        return contact;
    }

    public void setcontact(String contact) {
        this.contact = contact;
    }
}
