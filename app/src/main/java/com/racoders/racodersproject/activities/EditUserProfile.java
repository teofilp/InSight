package com.racoders.racodersproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.racoders.racodersproject.classes.User;
import com.racoders.racodersproject.fragments.Profile;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class EditUserProfile extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatEditText displayName;
    AppCompatEditText newPassword;
    AppCompatEditText oldPassword;
    CircleImageView profileImage;

    User currentUser;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Crop.pickImage(EditUserProfile.this);
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
        setContentView(R.layout.activity_edit_user_profile);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        displayName = findViewById(R.id.displayName);
        newPassword = findViewById(R.id.newPassword);
        profileImage = findViewById(R.id.userProfileImage);
        oldPassword = findViewById(R.id.oldPassword);
        toolbar = findViewById(R.id.toolbar);

        setTitle("Edit Profile");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    Crop.pickImage(EditUserProfile.this);
                else
                    ActivityCompat.requestPermissions(EditUserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });

        downloadUserData();

    }

    private void downloadUserData() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    currentUser = dataSnapshot.getValue(User.class);
                    String userCurrentDisplayName = currentUser.getDisplayName();

                    displayName.setText(userCurrentDisplayName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        StorageReference reference = FirebaseStorage.getInstance().getReference().child("images/users/" + userId + ".jpeg");
        GlideApp.with(getApplicationContext()).load(reference).into(profileImage);

    }

    public void saveNewProfile(View view){

        final String newPasswordString = newPassword.getText().toString();
        String oldPasswordString = oldPassword.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        if(newPasswordString.length() >= 8 && oldPasswordString.length() >=8){
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPasswordString);
            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPasswordString).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            saveUserNewProfileInfo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditUserProfile.this, "Somehing went wrong.. try again later", Toast.LENGTH_SHORT).show();
                            Log.i("password error:", e.toString());
                        }
                    });
                }
            });

        } else {
            saveUserNewProfileInfo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

    }

    public void saveUserNewProfileInfo(final String id){

            currentUser.setDisplayName(displayName.getText().toString());

            FirebaseDatabase.getInstance().getReference().child("Users").child(id).setValue(currentUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){


                        final StorageReference spaceref = FirebaseStorage.getInstance().getReference().child("images").child("users").child(id + ".jpeg");

                        spaceref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditUserProfile.this, "couldn;t delete the image", Toast.LENGTH_SHORT).show();
                            }
                        });

                        profileImage.setDrawingCacheEnabled(true);
                        profileImage.buildDrawingCache();

                        final Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        final byte[] data = baos.toByteArray();

                        final UploadTask uploadTask = spaceref.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditUserProfile.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(EditUserProfile.this, "All good", Toast.LENGTH_SHORT).show();
                                Profile.getNameTextView().setText(currentUser.getDisplayName());
                                Profile.getProfileImageView().setImageBitmap(bitmap);

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.get(getApplicationContext()).clearDiskCache();
                                    }
                                });
                                
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(EditUserProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }


}
