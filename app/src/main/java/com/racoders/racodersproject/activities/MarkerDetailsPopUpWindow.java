package com.racoders.racodersproject.activities;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.fragments.UserMapFragment;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.fragments.UserNewsfeedFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class MarkerDetailsPopUpWindow extends Activity{

    private CircleImageView locationImage;
    private ImageView toggleFavoriteImageView;
    private TextView address;
    private TextView title;
    private String id;
    private final double WIDTH_RATIO = 0.9;
    private final double HEIGHT_RATIO = 0.35;
    private Set<String> mFavLocationsSet;
    private PointOfInterest pointOfInterest;
    private Button createRoute;
    private String key;
    private boolean isFav = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_details_pop_up_window);

        createRoute = findViewById(R.id.createRoute);
        locationImage = findViewById(R.id.locationImage);
        toggleFavoriteImageView = findViewById(R.id.toggleFavoriteImage);
        address = findViewById(R.id.address);

        createRoute.setEnabled(false);

        id = getIntent().getStringExtra("id");

        loadImage(id);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        width*= WIDTH_RATIO;
        height*= HEIGHT_RATIO;

        getWindow().setLayout(width, height);

        title = findViewById(R.id.title);

        /**
         * check for new user
         * if user already exists get the Array of fav locations
         * else create a new Array
         */
        if(UserMapFragment.getmFavLocationsString()!=null){
            mFavLocationsSet = new HashSet<>(UserMapFragment.getmFavLocationsString());
            if(mFavLocationsSet.contains(id)){
                toggleFavoriteImageView.setBackground(getResources().getDrawable(R.drawable.bordered_fav_bookmark));
                isFav = true;
            } else{
                toggleFavoriteImageView.setBackground(getResources().getDrawable(R.drawable.not_favorite));
            }

        }else{
                mFavLocationsSet = new HashSet<>();
        }
        if(id!=null){
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(id);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        pointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                        createRoute.setEnabled(true);
                        title.setText(pointOfInterest.getTitle());
                        address.setText(pointOfInterest.getAdress());
                        key = dataSnapshot.getKey();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            //finish();
        }

    }
    public void toggleFavorite(View view){
        final View mView = view;

        if(isFav){
            mFavLocationsSet.remove(id);
            List<String> mList = new ArrayList<>(mFavLocationsSet);
            UserMapFragment.setmFavLocationsString(mList);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());

            databaseReference.setValue(mList, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        mView.setBackground(getResources().getDrawable(R.drawable.not_favorite));
                        isFav = false;
                    }
                }
            });

        }else{
            mFavLocationsSet.add(id);
            List<String> mList = new ArrayList<>(mFavLocationsSet);
            UserMapFragment.setmFavLocationsString(mList);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());

            databaseReference.setValue(mList, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        mView.setBackground(getResources().getDrawable(R.drawable.bordered_fav_bookmark));
                        isFav = true;
                    }
                }
            });

        }

        UserNewsfeedFragment.getInstance().populateRecyclerViewWithFavoriteNews();
    }
    public void createRoute(View view){
        UserMapFragment.loadRouteInfo(key, pointOfInterest);
        UserMapFragment.createRoute(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()));
        finish();
    }

    public void showLocationProfile(View view){
        String[] keys = key.split("/");
        String mKey = keys[keys.length-1];
        startActivity(new Intent(getApplicationContext(), PublicLocationProfile.class).putExtra("id", mKey));

    }

    private void loadImage(String key){
        if(key == null)
            finish();
        else{
            String[] keys = key.split("/");
            String mKey = keys[keys.length-1];

            StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + mKey + ".jpeg");
            GlideApp.with(getApplicationContext()).load(storage).into(locationImage);
        }


    }




}
