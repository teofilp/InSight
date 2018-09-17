package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.racoders.racodersproject.R;

public class toggleLoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_login);

    }

    public void toNormalSignIn(View view){
        startActivity(new Intent(getApplicationContext(), UserSignIn.class));
        finish();
    }

    public void toAdminSignIn(View view){
        startActivity(new Intent(getApplicationContext(), AdminSignIn.class));
        finish();
    }
}
