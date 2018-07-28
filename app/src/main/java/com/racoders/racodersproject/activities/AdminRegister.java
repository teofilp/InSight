
package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.racoders.racodersproject.R;

public class AdminRegister extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AppCompatEditText displayName;
    private AppCompatEditText email;
    private AppCompatEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.AdminBlue));
        }
        
        displayName = findViewById(R.id.displayName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onSubmit(View view){

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
    public void completionResult(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information

            startActivity(new Intent(getApplicationContext(), AddLocation.class));
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(AdminRegister.this, "Authentication failed. Check your credentials or try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AdminSignIn.class));
        finish();
    }
}
