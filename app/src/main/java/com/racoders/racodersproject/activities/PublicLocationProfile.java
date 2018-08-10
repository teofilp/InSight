package com.racoders.racodersproject.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.PointOfInterest;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PublicLocationProfile extends AppCompatActivity {
    private CircleImageView profile_photo;
    private TextView profileName;
    private TextView category;
    private TextView location;

    private PointOfInterest pointOfInterest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_location_profile);

        profile_photo = findViewById(R.id.public_profile_image);
        profileName = findViewById(R.id.profileName);
        category = findViewById(R.id.category);
        location = findViewById(R.id.location);

        final String id = getIntent().getStringExtra("id");

        DatabaseReference getPOisDBRef = FirebaseDatabase.getInstance().getReference().child("POIs");
        getPOisDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dsChilds : dataSnapshot.getChildren())
                        for(DataSnapshot childsOfdsChilds : dsChilds.getChildren())
                            if(childsOfdsChilds.getKey().equals(id)){
                                pointOfInterest = childsOfdsChilds.getValue(PointOfInterest.class);
                                updateInfo(pointOfInterest, id);
                            }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void updateInfo(PointOfInterest pointOfInterest, String id){
        updateProfileImage(id);
        profileName.setText(pointOfInterest.getTitle());
        category.setText(pointOfInterest.getCategory());
        location.setText(pointOfInterest.getAdress());

    }

    private void updateProfileImage(String id){
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + id + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profile_photo);
    }

}
