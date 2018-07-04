package com.racoders.racodersproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MarkerDetailsPopUpWindow extends Activity{

    private TextView title;
    private TextView description;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_details_pop_up_window);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        width*=.80;
        height*=.70;
        System.out.println(width + " " + height);
        getWindow().setLayout(width, height);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        String id = getIntent().getStringExtra("id");
        System.out.println(id);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("POIs").child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    FavoriteLocation favoriteLocation = dataSnapshot.getValue(FavoriteLocation.class);
                    title.setText(favoriteLocation.getTitle());
                    description.setText(favoriteLocation.getDescription());
                    System.out.println(favoriteLocation.getTitle());
                    System.out.println(favoriteLocation.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
