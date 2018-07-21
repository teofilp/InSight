package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.racoders.racodersproject.R;

public class AdminBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_board);
    }

    public void toAddNews(View view){
        startActivity(new Intent(getApplicationContext(), AddNews.class));
    }
    public void toAddEvent(View view){
        //startActivity(new Intent(getApplicationContext(), AddEvent.class));
    }
    public void toAddLocation(View view){
        startActivity(new Intent(getApplicationContext(), AddLocation.class));
    }
}
