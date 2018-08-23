package com.racoders.racodersproject.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.LocationCustomAdapter;
import com.racoders.racodersproject.classes.PointOfInterest;

import java.util.ArrayList;
import java.util.List;

public class LocationWithSpecificCategory extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LocationCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_with_specific_category);

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);

        final String category = getIntent().getStringExtra("categoryName");
        toolbar.setTitle(category);

        setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        getAllLocationsWithGivenCategory(category);
    }

    private void getAllLocationsWithGivenCategory(String category){

        final List<PointOfInterest> list = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("POIs").child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren())
                        list.add(child.getValue(PointOfInterest.class));
                }
                adapter = new LocationCustomAdapter(list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
