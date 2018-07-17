package com.racoders.racodersproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
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

import java.util.Arrays;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    SupportMapFragment mapFragment;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    static LatLng myLocation;
    public static ImageButton imageButton;
    private int contor=0;
    public static Marker me;
    public static HashMap<Marker, String> mMarkers = new HashMap<>();
    public static boolean isFavOnly = true;
    public static Button markersState;
    static String[] s;
    private Spinner myFilters;
    public static String activeFilter = "All";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_fragment1);
        if(mMap == null)
            mapFragment.getMapAsync(this);

        imageButton = view.findViewById(R.id.locationTrackerButton);
        markersState = view.findViewById(R.id.markersState);
        imageButton.setEnabled(false);
        myFilters = view.findViewById(R.id.filter_spinner);
        checkForNewUser(FirebaseAuth.getInstance().getUid());

        return view;

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        isFavOnly = true;
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(me)) {
                    System.out.println("markerCLicked");
                    String markerId = mMarkers.get(marker);
                    startActivity(new Intent(getApplicationContext(), MarkerDetailsPopUpWindow.class).putExtra("id", markerId));
                } else {
                    Toast.makeText(getContext(), "This is your location", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
            if(!success){
                Log.i("error", "Style parsing failed");
            }
        }catch(Resources.NotFoundException e){
            Log.i("error", "Can't find the style: " + e.toString());
        }

        locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);

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

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        getFavPOIS();
        ArrayAdapter<CharSequence> myFilterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter, android.R.layout.simple_spinner_item);
        myFilters.setAdapter(myFilterAdapter);
        myFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        activeFilter = "All";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS();
                        else
                            getAllPOIS();
                        break;
                    case 1:
                        activeFilter = "Restaurants";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS(activeFilter);
                        else
                            getAllPOIS("Restaurants");
                        break;
                    case 2:
                        activeFilter = "Transport";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS(activeFilter);
                        else
                            getAllPOIS(activeFilter);
                        break;
                    case 3:
                        activeFilter = "Pubs";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS(activeFilter);
                        else
                            getAllPOIS(activeFilter);
                        break;
                    case 4:
                        activeFilter = "Sport";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS(activeFilter);
                        else
                            getAllPOIS(activeFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMap!=null && myLocation!=null){
            mMap.clear();
            me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            if(markersState.getText().equals("Favorite Locations"))
                if(!activeFilter.equals("All"))
                    getAllPOIS(activeFilter);
                else
                    getAllPOIS();
            else
                getFavPOIS();
        }
    }

    public static void getFavPOIS(){
        DatabaseReference myLocations = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid()).child("0");
        myLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myString = dataSnapshot.getValue(String.class);
                    if(!myString.equals("")){
//                        System.out.println(myString);
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

    public static void getFavPOIS(final String category){
        DatabaseReference myLocations = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid()).child("0");
        myLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myString = dataSnapshot.getValue(String.class);
                    if(!myString.equals("")){
//                        System.out.println(myString);
                        s = myString.split("%");
                        for(String str : s){
                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs").child(str);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        PointOfInterest favPointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                                        if(favPointOfInterest.getCategory().equals(category)){
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            mMarkers.put(marker, dataSnapshot.getKey());
                                        }else if(favPointOfInterest.getCategory().equals("All"))
                                            getFavPOIS();
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

    public static void getAllPOIS(){
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
    public static void checkForNewUser(String Uid){
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
                                Toast.makeText(getApplicationContext(), "Could create new record, try again later", Toast.LENGTH_SHORT).show();
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
    public static void getAllPOIS(final String category){
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
                        if(pointOfInterest.getCategory().equals(category)){
                            // check if the pois is favorite or not
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void reloadMap(){
        mMap.clear();
        if(myLocation!=null){
            me = mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
        }
    }


}




