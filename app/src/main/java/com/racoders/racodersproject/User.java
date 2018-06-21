package com.racoders.racodersproject;

import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private String displayName;
    private String email;
    private String userName;
    private String password;
    private String socialID;

    public User(){

    }
    public String getSocialID(){
        return socialID;
    }
    public void setSocialID(String value){
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

}
