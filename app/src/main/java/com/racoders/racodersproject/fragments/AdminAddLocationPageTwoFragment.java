package com.racoders.racodersproject.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.AddLocation;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminAddLocationPageTwoFragment extends Fragment implements OnMapReadyCallback,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener{

    public static GoogleMap mMap;
    private Marker marker;
    private SupportMapFragment mapFragment;
    private static LatLng locationLatLng;
    private static EditText locationAddress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_add_location_page_two_fragment, container, false);

        locationAddress = view.findViewById(R.id.locationAddress);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(mMap == null)
            mapFragment.getMapAsync(this);
        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),  android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        }
        else
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if(marker!=null)
            marker.remove();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        locationLatLng = latLng;
        AddLocation.locationLatLng = locationLatLng;
    }



    public static LatLng getLocationLatLng(){
        return locationLatLng;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.remove();
        return false;
    }

    public static String getLocationAddress(){
        return locationAddress.getText().toString();
    }


}
