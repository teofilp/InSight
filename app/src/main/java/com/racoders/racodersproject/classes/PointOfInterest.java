package com.racoders.racodersproject.classes;

import com.google.android.gms.maps.model.LatLng;

public class PointOfInterest {
    private String title;
    private String description;
    private String phoneNumber;
    private String emailAddress;
    private double latitude;
    private double longitude;
    private String adress;
    private String category;
    private String key;
    private float ratingSum;
    private int ratingNumb;

    public float getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(float ratingSum) {
        this.ratingSum = ratingSum;
    }

    public int getRatingNumb() {
        return ratingNumb;
    }

    public void setRatingNumb(int ratingNumb) {
        this.ratingNumb = ratingNumb;
    }

    public void setKey(String value)
    {
        key = value;
    }
    public String getKey() {
        return key;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public PointOfInterest(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
