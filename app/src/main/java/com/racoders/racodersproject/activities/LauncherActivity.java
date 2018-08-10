package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;

public class LauncherActivity extends AppCompatActivity {

    private ProgressBar launcherProgressBar;
    private ImageView appLogo;
    private ImageView reloadButton;

    Animation alphaAnimation;

    private int isEnterprise = 0; // 0 = unassigned, 1 = true, 2 = false
    private int loggedIn = 0; // 0 = unassigned, 1 = true, 2 = false
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        launcherProgressBar = findViewById(R.id.launcherProgressBar);
        appLogo = findViewById(R.id.launcher_app_logo);
        reloadButton = findViewById(R.id.reloadButton);

        retrieveUser();

        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.animate_alpha);
        appLogo.startAnimation(alphaAnimation);

        getTimer().start();
    }

    public void reload(View view){

        reloadButton.setVisibility(View.GONE);
        launcherProgressBar.setVisibility(View.VISIBLE);
        appLogo.startAnimation(alphaAnimation);
        retrieveUser();
        getTimer().start();

    }

    private CountDownTimer getTimer(){

        return new CountDownTimer(10000, 1000){
            @Override
            public void onTick(long l) {
                if(loggedIn == 2 && l < 7000){
                    cancel();
                    startActivity(new Intent(getApplicationContext(), toggleLoginActivity.class));
                    finish();
                }else if(loggedIn == 1 && isEnterprise == 1){
                    cancel();
                    startActivity(new Intent(getApplicationContext(), AdminPanel.class));
                    finish();
                }else if(loggedIn == 1 && isEnterprise == 2){
                    cancel();
                    startActivity(new Intent(getApplicationContext(), loggedInUser.class));
                    finish();
                }

            }

            @Override
            public void onFinish() {
                launcherProgressBar.setVisibility(View.GONE);
                appLogo.getAnimation().cancel();
                reloadButton.setVisibility(View.VISIBLE);

            }
        };
    }

    private void retrieveUser(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            loggedIn = 1;
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        for(DataSnapshot childOfChild : child.getChildren()){
                            if(childOfChild.exists()){
                                if(childOfChild.getKey().equals(userId)){
                                    isEnterprise = 1;
                                    break;
                                }

                            }
                        }
                    }
                    if(isEnterprise == 0)
                    {
                        isEnterprise = 2;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            loggedIn = 2;
        }
    }

}
