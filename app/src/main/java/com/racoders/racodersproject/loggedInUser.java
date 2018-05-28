package com.racoders.racodersproject;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.facebook.login.LoginManager;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.racoders.racodersproject.MainActivity.mAuth;
import static com.racoders.racodersproject.MainActivity.user;

public class loggedInUser extends AppCompatActivity {
    public Button logOut;
    private TextView username;
    private TextView phoneNumber;
    private TextView otherInfo;
    private Uri imageUri;
    private Bitmap bitmap;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_user);
        logOut = findViewById(R.id.logOut);
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.phoneNumber);
        otherInfo = findViewById(R.id.otherInfo);
        profileImage = findViewById(R.id.profileImage);
        username.setText(MainActivity.user.getDisplayName());
        phoneNumber.setText(MainActivity.user.getPhoneNumber());
        otherInfo.setText(MainActivity.user.getEmail());
        imageUri = user.getPhotoUrl();
//        try{
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            profileImage.setImageBitmap(bitmap);
//        }catch(IOException e){
//            profileImage.setImageResource(R.drawable.no_picture_available);
//        }
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

        Map<String, String> values = new HashMap<>();

        values.put("id", mAuth.getCurrentUser().getUid());
        values.put("name", username.getText().toString());
        values.put("email", otherInfo.getText().toString());

        dbref.push().setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    Toast.makeText(loggedInUser.this, "Successfully updated info", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void signOut(View v) {
        Log.i("info about user", mAuth.getCurrentUser().getDisplayName() + " " + mAuth.getCurrentUser().getPhoneNumber());
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        logOut.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);



    }
}
