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
import java.util.Arrays;



public class MarkerDetailsPopUpWindow extends Activity{

    private TextView title;
    private TextView description;
    private Button button;
    private String id;
    private final double WIDTH_RATIO = 0.8;
    private final double HEIGHT_RATIO = 0.7;

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

//        if(MapFragment.s!=null && Arrays.asList(MapFragment.s).contains(id)){
//            button.setText("Remove from favorites");
//            button.setBackgroundColor(getResources().getColor(R.color.red));
//        }
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
//    public void toggleFavorite(View view){
//        button = (Button) view;
//
//        final DatabaseReference getFavLocations = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid()).child("0");
//        getFavLocations.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    String myString = dataSnapshot.getValue(String.class);
//                    if(button.getText().equals("Add to favorites")){
//                        if(myString.equals("")){
//                            myString=id+"%";
//                            MapFragment.s = myString.split("%");
//                        }else{
//                            myString+=id+"%";
//                            MapFragment.s = myString.split("%");
//                        }
//                    }else{
//                        String aux = "";
//                        MapFragment.s = myString.split("%");
//                        for(String str : MapFragment.s){
//                            if(!str.equals(id))
//                                aux+=str+"%";
//                        }
//                        myString = aux;
//                        MapFragment.s = myString.split("%");
//                    }
//
//                    getFavLocations.setValue(myString, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                            if(databaseError == null){
//                                if(button.getText().equals("Add to favorites")){
//                                    button.setText("Remove from favorites");
//                                    button.setBackgroundColor(getResources().getColor(R.color.red));
//                                }else{
//                                    button.setText("Add to favorites");
//                                    button.setBackgroundColor(getResources().getColor(R.color.green));
//                                }
//                            }
//                        }
//                    });
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//    }


}
