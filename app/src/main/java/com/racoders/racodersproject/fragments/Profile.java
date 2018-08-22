package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.FavoriteLocationCustomAdapter;
import com.racoders.racodersproject.classes.PointOfInterest;
import com.racoders.racodersproject.classes.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Profile extends Fragment {
    private View view;
    private CircleImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private RecyclerView favoriteLocationsRecyclerView;
    private FavoriteLocationCustomAdapter adapter;


    
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
        favoriteLocationsRecyclerView = view.findViewById(R.id.profileFavoriteLocations);

        favoriteLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoriteLocationsRecyclerView.setNestedScrollingEnabled(false);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserInfo(id);
        getUserProfileImage(id);
        getFavoriteLocations(id);

        return view;
    }

    private void getFavoriteLocations(String id) {

        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                    List<String> list = dataSnapshot.getValue(t);
                    getLocationsForEachKey(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getLocationsForEachKey(List<String> list) {
        final List<PointOfInterest> myList = new ArrayList<>();
        for(String str : list){
            System.out.println(str);
            FirebaseDatabase.getInstance().getReferenceFromUrl(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        PointOfInterest current = dataSnapshot.getValue(PointOfInterest.class);

                        myList.add(current);
                        adapter = new FavoriteLocationCustomAdapter(myList);
                        favoriteLocationsRecyclerView.setAdapter(adapter);
                    } else{

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }



    private void getUserInfo(String id) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser.getSocialID() != null){
                        getFacebookUser();
                    } else {
                        setUserInfo(currentUser.getDisplayName(), currentUser.getEmail());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFacebookUser() {
        String facebookId = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        for(UserInfo profile : user.getProviderData()){
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())){
                facebookId = profile.getUid();
                setUserInfo(profile.getDisplayName(), profile.getEmail());
            }
        }
        String photoUrl = "https://graph.facebook.com/" + facebookId + "/picture?height=500";
        GlideApp.with(getApplicationContext()).load(photoUrl).into(profileImageView);
    }

    private void getUserProfileImage(String id) {
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/users/" + id + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profileImageView);
    }

    private void setUserInfo(String name, String email){
        String nameString = "Name: " + name;
        String emailString = "Email: " + email;
        nameTextView.setText(nameString);
        emailTextView.setText(emailString);
    }

}
