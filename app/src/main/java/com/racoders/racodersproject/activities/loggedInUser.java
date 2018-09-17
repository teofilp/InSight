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

import com.racoders.racodersproject.classes.MapLocationToggleHandler;
import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.CategoriesFragment;
import com.racoders.racodersproject.fragments.UserMapFragment;
import com.racoders.racodersproject.fragments.UserNewsfeedFragment;


import static com.racoders.racodersproject.fragments.UserMapFragment.activeFilter;
import static com.racoders.racodersproject.fragments.UserMapFragment.getAllPOIS;
import static com.racoders.racodersproject.fragments.UserMapFragment.getAllTextView;
import static com.racoders.racodersproject.fragments.UserMapFragment.getAnimationRadioButton;
import static com.racoders.racodersproject.fragments.UserMapFragment.getFavPOIS;
import static com.racoders.racodersproject.fragments.UserMapFragment.getFavTextView;
import static com.racoders.racodersproject.fragments.UserMapFragment.isFavOnly;
import static com.racoders.racodersproject.fragments.UserMapFragment.mFavPOIs;
import static com.racoders.racodersproject.fragments.UserMapFragment.mMap;
import static com.racoders.racodersproject.fragments.UserMapFragment.mMarkers;
import static com.racoders.racodersproject.fragments.UserMapFragment.myLocation;
import static com.racoders.racodersproject.fragments.UserMapFragment.reloadMap;
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
                    UserMapFragment.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, UserMapFragment.locationListener);
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

        adapter.addFragment(new UserNewsfeedFragment(), "");
        adapter.addFragment(new CategoriesFragment(), "");
        adapter.addFragment(new UserMapFragment(), "");
        adapter.addFragment(new Profile(), "");

        viewPager.setAdapter(adapter);

        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tablayout_feed_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.tablayout_categories_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.tablayout_map_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.tablayout_person_icon);
    }
    public void moveCameraToMe(final View view){

        view.animate().rotationBy(360).setDuration(500);
        UserMapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));

    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), UserSignIn.class);
        startActivity(intent);
        mMap.clear();
        mMarkers.clear();
        mFavPOIs.clear();
        isFavOnly = true;
        UserMapFragment.setmFavLocationsString(null);
        UserMapFragment.mMap = null;
        finish();
    }

    @Override
    public void onBackPressed() {
        onStop();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserMapFragment.mMap = null;
    }

    public void toggleAll(View view){

        MapLocationToggleHandler.setContext(getApplicationContext());
        isFavOnly = MapLocationToggleHandler.toggleAll(getAllTextView(), getFavTextView(), getAnimationRadioButton(), isFavOnly);

        reloadMap();
        if(activeFilter.equals("All"))
            getAllPOIS();
        else
            getAllPOIS(activeFilter);

    }

    public void toggleFavorite(View view){

        MapLocationToggleHandler.setContext(getApplicationContext());
        isFavOnly = MapLocationToggleHandler.toggleFavorite(getAllTextView(), getFavTextView(), getAnimationRadioButton(), isFavOnly);

        reloadMap();
        if(activeFilter.equals("All"))
            getFavPOIS();
        else
            getFavPOIS(activeFilter);

    }

    public void toEditProfile(View view){
        startActivity(new Intent(getApplicationContext(), EditUserProfile.class));
    }
}
