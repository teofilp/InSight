package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;

public class toggleLoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_login);

    }

    public void toNormalSignIn(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void toAdminSignIn(View view){
        startActivity(new Intent(getApplicationContext(), AdminSignIn.class));
    }
}
