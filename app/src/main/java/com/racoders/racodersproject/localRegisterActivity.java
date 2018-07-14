package com.racoders.racodersproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class localRegisterActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    private AppCompatEditText displayName;
    private AppCompatEditText email;
    private AppCompatEditText password;


    public void onSubmit(View view) {

        String displayNameString = displayName.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if (displayNameString.length() == 0) {
            Toast.makeText(this, "Display name is required", Toast.LENGTH_SHORT).show();
        } else if (emailString.length() == 0) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        } else if (passwordString.length() < 7) {
            Toast.makeText(this, "Password is required, at least 7 characters", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) { completionResult(task);
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_register);

        displayName = findViewById(R.id.displayName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }
    }
    public void completionResult(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            user = mAuth.getCurrentUser();
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(localRegisterActivity.this, "Authentication failed. Check your credentials or try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
