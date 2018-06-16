package com.racoders.racodersproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText email;


    public void register(View view){
        String usernameString = username.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if(usernameString.length()>5 && passwordString.length() > 6 && emailString.length()>0){
            Log.i("Successfully registered",
                    "username: " + usernameString + " email: " + emailString + " password: " + passwordString);
        }else
            Toast.makeText(this, "Check your input details again. They may be to short or invalid", Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Welcome to our test app", Toast.LENGTH_SHORT).show();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
    }
}
