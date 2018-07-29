package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.PointOfInterest;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminProfile extends Fragment implements OnMapReadyCallback {

    private ImageView profileImage;
    private TextView title;
    private TextView description;
    private RatingBar ratingBar;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private PointOfInterest me;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_profile, container, false);
        profileImage = view.findViewById(R.id.profileImage);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.locationDescription);
        ratingBar = view.findViewById(R.id.RatingBar);


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.profileMap);
        if(mGoogleMap == null){
            mapFragment.getMapAsync(this);
        }
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profileImage);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshotChild : dataSnapshot.getChildren())
                        for(DataSnapshot childOfChild : snapshotChild.getChildren())
                            if(childOfChild.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                me = childOfChild.getValue(PointOfInterest.class);
                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(me.getLatitude(), me.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));

                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(me.getLatitude(), me.getLongitude()), 15));
                                title.setText(me.getTitle());
                                Spanned sp = Html.fromHtml(me.getDescription());
                                description.setText(sp);
                                ratingBar.setRating(me.getRatingSum()/me.getRatingNumb());

                            }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if(mGoogleMap!=null){
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshotChild : dataSnapshot.getChildren())
                            for(DataSnapshot childOfChild : snapshotChild.getChildren())
                                if(childOfChild.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    me = childOfChild.getValue(PointOfInterest.class);
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(me.getLatitude(), me.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));

                                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(me.getLatitude(), me.getLongitude()), 15));
                                    title.setText(me.getTitle());
                                    Spanned sp = Html.fromHtml(me.getDescription());
                                    description.setText(sp);
                                    ratingBar.setRating(me.getRatingSum()/me.getRatingNumb());

                                }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
