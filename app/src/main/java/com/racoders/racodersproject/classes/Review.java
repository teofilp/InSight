package com.racoders.racodersproject.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Review {

    private String author;
    private String description;
    private int rating;
    private Date date;

    public Review(){

    }

    public Review( String author, String description, int rating, Date date) {
        this.author = author;
        this.description = description;
        this.rating = rating;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public Date getDate(){
        return date;
    }

    public void save(String locationId){
        FirebaseDatabase.getInstance().getReference().child("Reviews").child(locationId)
                .push().setValue(this, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null)
                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
