package com.racoders.racodersproject.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.racoders.racodersproject.R;

import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.CategoriesFragment;
import com.racoders.racodersproject.fragments.MapFragment;
import com.racoders.racodersproject.fragments.newsFeed;


import static com.racoders.racodersproject.fragments.MapFragment.activeFilter;
import static com.racoders.racodersproject.fragments.MapFragment.getAllPOIS;
import static com.racoders.racodersproject.fragments.MapFragment.getFavPOIS;
import static com.racoders.racodersproject.fragments.MapFragment.isFavOnly;
import static com.racoders.racodersproject.fragments.MapFragment.mFavPOIs;
import static com.racoders.racodersproject.fragments.MapFragment.mMap;
import static com.racoders.racodersproject.fragments.MapFragment.mMarkers;
import static com.racoders.racodersproject.fragments.MapFragment.markersState;
import static com.racoders.racodersproject.fragments.MapFragment.reloadMap;
import com.racoders.racodersproject.fragments.Profile;

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

        adapter.addFragment(new newsFeed(), "");
        adapter.addFragment(new CategoriesFragment(), "");
        adapter.addFragment(new MapFragment(), "");
        adapter.addFragment(new Profile(), "");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tablayout_feed_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.tablayout_categories_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.tablayout_map_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.tablayout_person_icon);
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
        mMap.clear();
        mMarkers.clear();
        mFavPOIs.clear();
        isFavOnly = true;
        MapFragment.setmFavLocationsString(null);
        MapFragment.mMap = null;

        finish();

    }



}
