package com.racoders.racodersproject.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.AdminAddLocationPageOneFragment;
import com.racoders.racodersproject.fragments.AdminAddLocationPageThreeFragment;
import com.racoders.racodersproject.fragments.AdminAddLocationPageTwoFragment;

import static com.facebook.FacebookSdk.getApplicationContext;


public class AddLocation extends AppCompatActivity{

    private Button nt_button;
    private Button bk_button;
    private View[] dots;
    private int currTab = 0;

    ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                AdminAddLocationPageTwoFragment.mMap.setMyLocationEnabled(true);
            else{
                Toast.makeText(this, "We need your location in order no make it easier to add locations on the map", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);


        nt_button = findViewById(R.id.nt_button);
        bk_button = findViewById(R.id.bk_button);
        dots = new View[3];
        dots[0] = findViewById(R.id.firstPageDot);
        dots[1] = findViewById(R.id.secondPageDot);
        dots[2] = findViewById(R.id.thirdPageDot);

        viewPager = findViewById(R.id.addLocationViewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new AdminAddLocationPageOneFragment());
        pagerAdapter.addFragment(new AdminAddLocationPageTwoFragment());
        pagerAdapter.addFragment(new AdminAddLocationPageThreeFragment());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    bk_button.setVisibility(View.GONE);
                    dots[0].setBackground(getResources().getDrawable(R.drawable.round_dots));
                    dots[1].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[2].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                }else if(position ==1){
                    bk_button.setVisibility(View.VISIBLE);
                    dots[0].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[1].setBackground(getResources().getDrawable(R.drawable.round_dots));
                    dots[2].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                }else{
                    dots[0].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[1].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[2].setBackground(getResources().getDrawable(R.drawable.round_dots));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(pagerAdapter);

        bk_button.setVisibility(View.GONE);
        nt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currTab < 3){
                    viewPager.setCurrentItem(++currTab);

                    if(!bk_button.isEnabled())
                        bk_button.setEnabled(true);
                }
                else{
                    nt_button.setText("Save");
                }
            }
        });
        bk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currTab>0){
                    if(currTab == 1)
                        bk_button.setEnabled(false);
                    viewPager.setCurrentItem(--currTab);

                }else{

                }
            }
        });




    }








}
