package com.racoders.racodersproject.classes;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Review {

    private String author;
    private String description;
    private double rating;
    private Date date;

    public Review(){

    }

    public Review( String author, String description, double rating, Date date) {
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

    public double getRating() {
        return rating;
    }

    public Date getDate(){
        return date;
    }

    public void save(String locationId, final View startView, final View finalView){
        FirebaseDatabase.getInstance().getReference().child("Reviews").child(locationId)
                .child(this.getAuthor()).setValue(this, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                    startView.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startView.setVisibility(View.GONE);
                            finalView.setVisibility(View.VISIBLE);
                            finalView.animate().alpha(1).setDuration(200);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }

            }
        });
    }
}
