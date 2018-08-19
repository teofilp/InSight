package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.User;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Profile extends Fragment {
    private View view;
    private CircleImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    
    public Profile() {
    }

    /**
     StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + id + ".jpeg");
     GlideApp.with(getApplicationContext()).load(storage).into(authorImageView);

     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        profileImageView = view.findViewById(R.id.profileImage);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserInfo(id);
        getUserProfileImage(id);

        return view;
    }

    private void getUserInfo(String id) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User currentUser = dataSnapshot.getValue(User.class);
                    String nameString = "Name: " + currentUser.getDisplayName();
                    String emailString = "Email: " + currentUser.getEmail();
                    nameTextView.setText(nameString);
                    emailTextView.setText(emailString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserProfileImage(String id) {
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/users/" + id + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profileImageView);
    }

}
