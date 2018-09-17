package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.PointOfInterest;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminEditProfile extends AppCompatActivity {

    private CircleImageView profileImage;
    private AppCompatEditText description;
    private Toolbar toolbar;
    private String id;
    private PointOfInterest myPointOfInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImageView);
        description = findViewById(R.id.description);
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setSupportActionBar(toolbar);
        if(getActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        getAdminData();
    }


    private void getAdminData(){



        FirebaseDatabase.getInstance().getReference().child("POIs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot categories : dataSnapshot.getChildren()){
                                if(categories.exists()){
                                    for(DataSnapshot pois : categories.getChildren()){
                                        if(pois.getKey().equals(id)){
                                            myPointOfInterest = pois.getValue(PointOfInterest.class);
                                            if(myPointOfInterest.getDescription()!=null){
                                                String descriptionString = myPointOfInterest.getDescription();
                                                description.setText(descriptionString);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profileImage);


    }

    public void updateAdminProfile(View view){

        if(myPointOfInterest!=null){
            if(!myPointOfInterest.getDescription().equals(description.getText().toString())){
                myPointOfInterest.setDescription(description.getText().toString());
                FirebaseDatabase.getInstance().getReferenceFromUrl(myPointOfInterest.getKey())
                        .setValue(myPointOfInterest, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError==null){
                                    uploadImageProfile();
                                }
                            }
                        });
            }
        }



    }

    private void uploadImageProfile(){

        final StorageReference spaceref = FirebaseStorage.getInstance()
                .getReference().child("images/pois/" + FirebaseAuth.getInstance()
                        .getCurrentUser().getUid() + ".jpeg");
        profileImage.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminEditProfile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
