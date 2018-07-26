package com.racoders.racodersproject.activities;

import com.racoders.racodersproject.fragments.MapFragment;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.*;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MarkerDetailsPopUpWindow extends Activity{

    private TextView title;
    private TextView description;
    private Button button;
    private String id;
    private final double WIDTH_RATIO = 0.8;
    private final double HEIGHT_RATIO = 0.7;
    private Set<String> mFavLocationsSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_details_pop_up_window);
        button = findViewById(R.id.toggleFavoriteButton);

        id = getIntent().getStringExtra("id");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        width*= WIDTH_RATIO;
        height*= HEIGHT_RATIO;

        getWindow().setLayout(width, height);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        mFavLocationsSet = new HashSet<>(MapFragment.getmFavLocationsString());
        if(mFavLocationsSet.contains(id)){
            button.setText("Remove from favorites");
            button.setBackgroundColor(getResources().getColor(R.color.red));
        }

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    PointOfInterest pointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                    title.setText(pointOfInterest.getTitle());
                    description.setText(pointOfInterest.getDescription());
                    System.out.println(pointOfInterest.getTitle());
                    System.out.println(pointOfInterest.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void toggleFavorite(View view){
        Button mButton = (Button) view;

        if(mButton.getText().toString().equals("Remove from favorites")){
            mFavLocationsSet.remove(id);
            List<String> mList = new ArrayList<>(mFavLocationsSet);
            MapFragment.setmFavLocationsString(mList);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());

            databaseReference.setValue(mList, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        button.setText("Add to favorites");
                        button.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                }
            });

        }else{
            mFavLocationsSet.add(id);
            List<String> mList = new ArrayList<>(mFavLocationsSet);
            MapFragment.setmFavLocationsString(mList);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());

            databaseReference.setValue(mList, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        button.setText("Remove from favorites");
                        button.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                }
            });

        }
    }


}
