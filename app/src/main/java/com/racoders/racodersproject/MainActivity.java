package com.racoders.racodersproject;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    private EditText displayName;
    private EditText email;
    private EditText password;

    public void onSubmit(View view){

        String displayNameString = displayName.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if(displayNameString.length()==0){
            Toast.makeText(this, "Display name is required", Toast.LENGTH_SHORT).show();
        }else if(emailString.length()==0){
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
        }else if(passwordString.length()<7){
            Toast.makeText(this, "Password is required, at least 7 characters", Toast.LENGTH_LONG).show();
        }else{

            mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Successfully created account", Toast.LENGTH_SHORT).show();
                                user = mAuth.getCurrentUser();

                            }else{
                                Log.i("error", task.getException().toString());
                                Toast.makeText(MainActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayName = findViewById(R.id.displayName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }


    private void toLoggedInActivity(){
        Intent intent = new Intent(getApplicationContext(), loggedInUser.class);
        startActivity(intent);
    }
    @Override
    public void onStart(){
        super.onStart();
        user = mAuth.getCurrentUser();
        if(user!=null){
            toLoggedInActivity();
        }
    }


}
