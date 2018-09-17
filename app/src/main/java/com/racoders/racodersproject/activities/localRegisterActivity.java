package com.racoders.racodersproject.activities;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.racoders.racodersproject.R;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.racoders.racodersproject.classes.User;

public class localRegisterActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    private AppCompatEditText displayName;
    private AppCompatEditText email;
    private AppCompatEditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_register);

        displayName = findViewById(R.id.displayName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onSubmit(View view) {

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

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

    private void completionResult(Task<AuthResult> task){


        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            user = mAuth.getCurrentUser();
            saveInfo(mAuth.getCurrentUser().getUid());
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(localRegisterActivity.this, "Authentication failed. Check your credentials or try again later",
                    Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);

        }
    }

    private void saveInfo(final String uid) {
        User currentUser = new User();

        currentUser.setEmail(email.getText().toString());
        currentUser.setDisplayName(displayName.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).setValue(currentUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){

                    findViewById(R.id.progressBar).setVisibility(View.GONE);

                    startActivity(new Intent(getApplicationContext(), FirstTimeSetUserProfileImage.class));
                }
            }
        });
    }
}
