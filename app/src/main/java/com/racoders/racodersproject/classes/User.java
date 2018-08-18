package com.racoders.racodersproject.classes;

import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private String displayName;
    private String email;
    private String userName;
    private String password;
    private Long socialID;
    private String myFavPlaces;

    public User(){

    }
    public Long getSocialID(){
        return socialID;
    }
    public void setSocialID(Long value){
        socialID = value;
    }
    public String getDisplayName(){
        return displayName;
    }
    public void setDisplayName(String value){
        displayName = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addFavPlace(String i){
        myFavPlaces+=i;
    }

    public String getMyFavPlaces() {
        return myFavPlaces;
    }

    public static String getCurrentUserDisplayName(String id){
        return id;
    }
}
