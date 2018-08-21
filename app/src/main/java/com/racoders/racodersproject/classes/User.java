package com.racoders.racodersproject.classes;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private String displayName;
    private String email;
    private String socialID;
    private String myFavPlaces;

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
