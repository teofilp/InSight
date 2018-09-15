package com.racoders.racodersproject.activities;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.PointOfInterest;

public class AdminMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final double WIDTH_RATIO = .7;
    private final double HEIGHT_RATIO = .5;
    private LatLng locationLatLng;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        id = getIntent().getStringExtra("id");

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        width*= WIDTH_RATIO;
        height*= HEIGHT_RATIO;

        getWindow().setLayout(width, height);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.admin_map_style));
            if(!success){
                Log.i("error", "Style parsing failed");
            }
        }catch(Resources.NotFoundException e){
            Log.i("error", "Can't find the style: " + e.toString());
        }

        FirebaseDatabase.getInstance().getReference().child("POIs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot categories : dataSnapshot.getChildren()){
                        for(DataSnapshot pois : categories.getChildren()){
                            if(pois.getKey().equals(id)){
                                PointOfInterest pointOfInterest = pois.getValue(PointOfInterest.class);
                                LatLng latLng = new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
