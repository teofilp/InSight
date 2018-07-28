package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.racoders.racodersproject.R;

public class AdminSignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AppCompatEditText email;
    private AppCompatEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_in);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.AdminBlue));
        }

    }
    public void signIn(View view){
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if(passwordValidation(passwordString) && emailValidation(emailString)){
            mAuth.signInWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            completionResult(task);
                        }
                    });
        } else {
            Toast.makeText(this, "Something went wrong, check your credentials again or try later", Toast.LENGTH_SHORT).show();
        }

    }
    public void completionResult(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            startActivity(new Intent(getApplicationContext(), AdminPanel.class));

        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(AdminSignIn.this, "Authentication failed. Check your credentials or try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean passwordValidation(String passwordString){
        return passwordString.length()>0;
    }

    public boolean emailValidation(String emailString){
        return emailString.length()>0 && emailString.contains("@");
    }
    public void toLocalRegister(View view){
        startActivity(new Intent(getApplicationContext(), AdminRegister.class));
        finish();
    }
}
