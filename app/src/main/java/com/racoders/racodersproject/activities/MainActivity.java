package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
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
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.localRegisterActivity;
import com.racoders.racodersproject.activities.loggedInUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static String FbUserID;
    private AppCompatEditText email;
    private AppCompatEditText password;
    private com.facebook.login.LoginManager FacebookLoginManager;
    private CallbackManager callbackManager;
    public void toLocalRegister(View view){
        startActivity(new Intent(getApplicationContext(), localRegisterActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button fb_login =findViewById(R.id.fb_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        FacebookLoginManager= com.facebook.login.LoginManager.getInstance();
        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile", "user_birthday"));
            }
        });
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        FacebookLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("result", "Successful" + loginResult.getAccessToken().getUserId()
                + " " + loginResult.getAccessToken().getToken());
                FbUserID = AccessToken.getCurrentAccessToken().getUserId();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("result", "Cancelled");
            }

            @Override
            public void onError(FacebookException error){
                Log.i("result", "Failed" + error.toString());
            }
        });
        Toast.makeText(this, "Welcome to our test app", Toast.LENGTH_SHORT).show();
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
            user = mAuth.getCurrentUser();
            toLoggedInActivity();
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(MainActivity.this, "Authentication failed. Check your credentials or try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean passwordValidation(String passwordString){
        return passwordString.length()>0;
    }

    public boolean emailValidation(String emailString){
        return emailString.length()>0 && emailString.contains("@");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        completionResult(task);
                    }
                });
    }
    private void toLoggedInActivity(){
        Intent intent = new Intent(getApplicationContext(), loggedInUser.class);
        startActivity(intent);
        finish();
    }

}
