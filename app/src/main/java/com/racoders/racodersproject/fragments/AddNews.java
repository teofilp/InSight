package com.racoders.racodersproject.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AddNews extends Fragment {
    private AppCompatEditText title;
    private AppCompatEditText author;
    private AppCompatEditText description;
    private Button saveButton;
    private Bitmap newsImage;
//    private Uri selectedImage;
    private static CircleImageView mImage;


    public static CircleImageView getmImage() {
        return mImage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_news, container, false);
        mImage = view.findViewById(R.id.mImage);
        title = view.findViewById(R.id.title);
        author = view.findViewById(R.id.author);
        description = view.findViewById(R.id.description);
        saveButton = view.findViewById(R.id.save_button);


        mImage.setImageDrawable(getResources().getDrawable(R.drawable.noimage));

        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Crop.pickImage(getActivity());
                }
            });
        else
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference().child("News")
                        .child(FirebaseAuth.getInstance().getUid());
                String mId = mDbref.push().getKey();
                String key = mDbref.toString();
                key+="/"+mId;

                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference spaceref = storage.getReference().child("images/" + mId + ".jpeg");

                mImage.setDrawingCacheEnabled(true);
                mImage.buildDrawingCache();

                Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();



                final News mNews = new News(title.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        description.getText().toString(), Calendar.getInstance().getTime(), mId,key, 0);

                System.out.println(mId);
                mDbref.child(mId).setValue(mNews, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null){
                            UploadTask uploadTask = spaceref.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    AdminNews.getmList().add(mNews);
                                    AdminProfile.getPostsNumber().setText(Integer.toString(AdminNews.getmList().size()));
                                    AdminNews.setAdapter(AdminNews.getmList());

                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        return view;
    }
}
