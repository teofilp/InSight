package com.racoders.racodersproject.activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.fragments.AddNews;
import com.racoders.racodersproject.fragments.AdminNews;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FirstTimeSetUserProfileImage extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profileImage;
    private ProgressBar progressBar;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Crop.pickImage(FirstTimeSetUserProfileImage.this);
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
        setContentView(R.layout.activity_first_time_set_user_profile_image);

        toolbar = findViewById(R.id.toolbar);
        profileImage = findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    Crop.pickImage(FirstTimeSetUserProfileImage.this);
                else
                    ActivityCompat.requestPermissions(FirstTimeSetUserProfileImage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        });

        toolbar.setTitle("Set your profile image");
        Toast.makeText(this, "Tap on the image to choose another one", Toast.LENGTH_SHORT).show();
        setSupportActionBar(toolbar);
    }

    public void saveImage(View view){

        toolbar.animate().alpha(0.3f).setDuration(300);
        profileImage.animate().alpha(0.3f).setDuration(300);
        view.animate().alpha(0.3f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final StorageReference spaceref = FirebaseStorage.getInstance().getReference().child("images").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        UploadTask uploadTask = spaceref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), loggedInUser.class));
                finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FirstTimeSetUserProfileImage.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), loggedInUser.class));
                finish();
            }
        });
    }
}
