package com.racoders.racodersproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class localSignInActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public void signIn(View view){
        String emailString = email.getText().toString();
        final String passwordString = password.getText().toString();
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));

                        }else{
                            Toast.makeText(localSignInActivity.this, "Something went wrong, check your credentials again or try later", Toast.LENGTH_LONG).show();
                            email.setText("");
                            password.setText("");
                        }
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_sign_in);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


    }
    @Override
    public void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }
}
