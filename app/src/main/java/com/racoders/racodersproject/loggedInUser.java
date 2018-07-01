package com.racoders.racodersproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import static com.racoders.racodersproject.MainActivity.mAuth;
import static com.racoders.racodersproject.MainActivity.user;

public class loggedInUser extends AppCompatActivity {
    public Button logOut;
    private TextView username;
    private TextView email;
    private ProfilePictureView profileImage;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_user);

//        setCurrentUserInfo();
//
//        logOut = findViewById(R.id.logOut);
//        username = findViewById(R.id.username);
//        email = findViewById(R.id.email);
//        profileImage = findViewById(R.id.profileImage);
//
//        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getEmail());
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    dbref.setValue(currentUser, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            if (databaseError == null) {
//                                Toast.makeText(loggedInUser.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
//                                setCurrentUserInfo();
//                                profileImage.setProfileId(currentUser.getSocialID());
//                                updateUI(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getSocialID());
//
//                            }else{
//                                Log.i("error: ", databaseError.getMessage());
//                            }
//                        }
//                    });
//                }
//                else{
//                    currentUser = dataSnapshot.getValue(User.class);
//                    profileImage.setProfileId(currentUser.getSocialID());
//                    updateUI(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getSocialID());
//                    Log.i("user info", currentUser.getDisplayName()+ " " + currentUser.getSocialID() + " " + currentUser.getEmail());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        dbref.addListenerForSingleValueEvent(eventListener);

    }
    public void signOut(View v) {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
////    public void updateUI(String displayName, String emailString, String socialID){
////        username.setText(displayName);
////        email.setText(emailString);
////        profileImage.setProfileId(socialID);
////    }
////    public void setCurrentUserInfo(){
////        currentUser = new User();
////        String emailString="N/A";
////
////        if(MainActivity.user!=null){
////            for(UserInfo profile: MainActivity.user.getProviderData()){
////                emailString = profile.getEmail();
////            }
////        }
////
////        emailString = emailString.replace('@', '_');
////        emailString = emailString.replace('.', '_');
////
////        currentUser.setEmail(emailString);
////        currentUser.setDisplayName(MainActivity.user.getDisplayName());
////        currentUser.setSocialID(MainActivity.FbUserID);
////    }


}
