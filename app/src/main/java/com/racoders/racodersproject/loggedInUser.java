package com.racoders.racodersproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.racoders.racodersproject.MapFragment;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import static com.facebook.FacebookSdk.getApplicationContext;
import static com.racoders.racodersproject.MainActivity.mAuth;
import static com.racoders.racodersproject.MainActivity.user;
import static com.racoders.racodersproject.MapFragment.activeFilter;
import static com.racoders.racodersproject.MapFragment.getAllPOIS;
import static com.racoders.racodersproject.MapFragment.getFavPOIS;
import static com.racoders.racodersproject.MapFragment.isFavOnly;
import static com.racoders.racodersproject.MapFragment.mMarkers;
import static com.racoders.racodersproject.MapFragment.markersState;
import static com.racoders.racodersproject.MapFragment.me;
import static com.racoders.racodersproject.MapFragment.reloadMap;

public class loggedInUser extends FragmentActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    MapFragment.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MapFragment.locationListener);
                }
            }else{
                Toast.makeText(this, "We need your location for..", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_user);



        viewPager = findViewById(R.id.viewPager);

        tabLayout = findViewById(R.id.tabLayout);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new newsFeed(), "News Feed");
        adapter.addFragment(new com.racoders.racodersproject.Profile(), "Profile");
        adapter.addFragment(new MapFragment(), "Map");
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map_fragment);
//        mapFragment.getMapAsync(this);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);



    }
    public void moveCameraToMe(View view){
        MapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapFragment.myLocation, 16));
    }
    public void changeState(View view){

        if(isFavOnly){
            isFavOnly = !isFavOnly;
            markersState.setText("Favorite Locations");
            reloadMap();
            if(activeFilter.equals("All"))
                getAllPOIS();
            else
                getAllPOIS(activeFilter);
        } else {
            isFavOnly = !isFavOnly;
            markersState.setText("All Locations");
            reloadMap();
            if(activeFilter.equals("All"))
                getFavPOIS();
            else
                getFavPOIS(activeFilter);
        }
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        MapFragment.s = null;

        finish();

    }



}
