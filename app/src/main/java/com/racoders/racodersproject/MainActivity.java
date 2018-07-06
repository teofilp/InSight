package com.racoders.racodersproject;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private com.facebook.login.LoginManager FacebookLoginManager;
    private CallbackManager callbackManager;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static String FbUserID;
    private Button fb_login;
    private AppCompatEditText email;
    private AppCompatEditText password;

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
        fb_login = findViewById(R.id.fb_login);
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Report", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Report", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            toLoggedInActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Report", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void toLoggedInActivity(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
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
    public void signIn(View view){
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        if(passwordString.length()>0 && emailString.length()>0){
            mAuth.signInWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user = mAuth.getCurrentUser();
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));

                            }else{
                                Toast.makeText(MainActivity.this, "Something went wrong, check your credentials again or try later", Toast.LENGTH_LONG).show();
                                email.setText("");
                                password.setText("");
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Something went wrong, check your credentials again or try later", Toast.LENGTH_SHORT).show();
        }

    }


}
