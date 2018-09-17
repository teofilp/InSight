package com.racoders.racodersproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.racoders.racodersproject.fragments.AdminProfile;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAdminProfile extends AppCompatActivity {

    private CircleImageView profileImage;
    private AppCompatEditText description;
    private Toolbar toolbar;
    private String id;
    private PointOfInterest myPointOfInterest;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Crop.pickImage(EditAdminProfile.this);
            }else
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Crop.of(source_uri, destination_uri).asSquare().start(this);
                profileImage.setImageURI(Crop.getOutput(data));

            }
            else if (requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode, data);
            }
        }
    }
    public void handle_crop(int code, Intent data){

        if(code == RESULT_OK){
            profileImage.setImageURI(Crop.getOutput(data));
        }
        else if (code == Crop.RESULT_ERROR){
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);

        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImageView);
        description = findViewById(R.id.description);
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setTitle("Edit Profile");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.AdminDarkBlue));
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    Crop.pickImage(EditAdminProfile.this);
                else
                    ActivityCompat.requestPermissions(EditAdminProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });

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
                                } else {
                                    Log.i("database error:", databaseError.toString());
                                }
                            }
                        });
            }
        } else {
            uploadImageProfile();
        }



    }

    private void uploadImageProfile(){

        final StorageReference spaceref = FirebaseStorage.getInstance()
                .getReference().child("images/pois/" + FirebaseAuth.getInstance()
                        .getCurrentUser().getUid() + ".jpeg");
        profileImage.buildDrawingCache();

        final Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
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
                Toast.makeText(EditAdminProfile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(getApplicationContext()).clearDiskCache();
                    }
                });

                AdminProfile.getProfileImage().setImageBitmap(bitmap);
                AdminProfile.getDescription().setText(description.getText().toString());

                finish();
            }
        });

    }
}