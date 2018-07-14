package com.racoders.racodersproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int LOCATION_REQUEST_CODE = 1;
    private LatLng myLocation;
    private ImageButton imageButton;
    private int contor=0;
    private Marker me;
    private HashMap<Marker, String> mMarkers = new HashMap<>();
    private boolean isFavOnly = true;
    private Button markersState;
    static String[] s;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkForNewUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imageButton = findViewById(R.id.locationTrackerButton);
        markersState = findViewById(R.id.markersState);
        imageButton.setEnabled(false);
        locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                if(imageButton.isEnabled()==false){
                    imageButton.setEnabled(true);
                }
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(me!=null)
                    me.remove();
                me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
                if(contor==0){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                    contor++;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
         } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(45.4324242, 26.4343242);
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if(!success){
                Log.i("error", "Style parsing failed");
            }
        }catch(Resources.NotFoundException e){
            Log.i("error", "Can't find the style: " + e.toString());
        }
        getFavPOIS();

    }
    public void moveCameraToMe(View view){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
    }

    @Override
    protected void onStart() {
        super.onStart();
        contor = 0;
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        s = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        if(!marker.equals(me)) {
            System.out.println("markerCLicked");
            String markerId = mMarkers.get(marker);
            startActivity(new Intent(getApplicationContext(), MarkerDetailsPopUpWindow.class).putExtra("id", markerId));
        } else {
            Toast.makeText(this, "This is your location", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    public void changeState(View view){

        if(isFavOnly){
            isFavOnly = false;
            markersState.setText("Favorite Locations");
            mMap.clear();
            me = mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            getAllPOIS();
        } else {
            isFavOnly = true;
            markersState.setText("All Locations");
            mMap.clear();
            me = mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            getFavPOIS();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mMap!=null){
            mMap.clear();
            me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            if(markersState.getText().equals("Favorite Locations"))
                getAllPOIS();
            else
                getFavPOIS();
        }
    }
    public void getFavPOIS(){
        DatabaseReference myLocations = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid()).child("0");
        myLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myString = dataSnapshot.getValue(String.class);
                    if(!myString.equals("")){
                        System.out.println(myString);
                        s = myString.split("%");
                        for(String str : s){

                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs").child(str);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        PointOfInterest favPointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        mMarkers.put(marker, dataSnapshot.getKey());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void getAllPOIS(){
        DatabaseReference allLocationsReference = FirebaseDatabase.getInstance().getReference().child("POIs");
        allLocationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMarkers.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Marker marker;
                        PointOfInterest pointOfInterest = child.getValue(PointOfInterest.class);
                        String id = child.getKey();

                        if(s!=null && Arrays.asList(s).contains(id) && s.length>0){
                            marker= mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }else{
                            marker= mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }
                        mMarkers.put(marker, id);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void checkForNewUser(String Uid){
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(Uid).child("0");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    dbref.setValue("", new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError==null){

                            } else {
                                Toast.makeText(MapsActivity.this, "Could create new record, try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
