package com.racoders.racodersproject.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.MarkerDetailsPopUpWindow;
import com.racoders.racodersproject.classes.DirectionsParser;
import com.racoders.racodersproject.classes.PointOfInterest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    SupportMapFragment mapFragment;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    public static LatLng myLocation;
    public static ImageButton imageButton;
    private int contor=0;
    public static Marker me;
    public static HashMap<Marker, String> mMarkers = new HashMap<>();
    public static boolean isFavOnly = true;
    public static Button markersState;
    private static List<String> mFavLocationsString;
    private Spinner myFilters;
    public static String activeFilter;
    private static String str;
    public static HashMap<String, PointOfInterest> mFavPOIs = new HashMap<>();
    private static boolean routeCreated = false;

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
        activeFilter = "All";
        imageButton = view.findViewById(R.id.locationTrackerButton);
        markersState = view.findViewById(R.id.markersState);
        imageButton.setEnabled(false);
        myFilters = view.findViewById(R.id.filter_spinner);
//        checkForNewUser(FirebaseAuth.getInstance().getUid());

        return view;

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(me)) {
                    System.out.println("markerCLicked");
                    String markerId = mMarkers.get(marker);
                    System.out.println(markerId);
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
                if(mMap!=null){
                    if(me!=null)
                        me.remove();
                    me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
                    if(contor==0){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
                        contor++;
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


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
                        activeFilter = "Restaurant";
                        reloadMap();
                        if(isFavOnly)
                            getFavPOIS(activeFilter);
                        else
                            getAllPOIS(activeFilter);
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
                        activeFilter = "Pub";
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
        if(mMap == null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMap!=null && myLocation!=null && !routeCreated){
            mMap.clear();
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            if(markersState.getText().equals("Favorite Locations")) {
                if (!activeFilter.equals("All"))
                    getAllPOIS(activeFilter);
                else
                    getAllPOIS();
            }
            else {
                if(!activeFilter.equals("All"))
                    getFavPOIS(activeFilter);
                else
                    getFavPOIS();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMap!=null)
            mMap.clear();
        locationManager.removeUpdates(locationListener);
    }

    public static void getFavPOIS(){
        mMap.clear();
        final DatabaseReference favDbRef = FirebaseDatabase.getInstance().getReference()
                .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());
        favDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMarkers.clear();
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                    mFavLocationsString = dataSnapshot.getValue(t);
                    System.out.println("mFavLocationsString size " + mFavLocationsString.size());
                    for(int i = 0; i< mFavLocationsString.size(); i++){
                        System.out.println("i: " + i);
                        str = mFavLocationsString.get(i);
                        System.out.println(str);
                        if(str!=null){
                            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        PointOfInterest favPointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(favPointOfInterest.getCategory(), true))));
                                        mMarkers.put(marker, favPointOfInterest.getKey());
                                        mFavPOIs.put(dataSnapshot.getKey(), favPointOfInterest);

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
        mMarkers.clear();
        mMap.clear();
        DatabaseReference myLocations = FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());
        myLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                    mFavLocationsString = dataSnapshot.getValue(t);
                    if(mFavLocationsString !=null){

                        for(int i = 0; i< mFavLocationsString.size(); i++){
                            str = mFavLocationsString.get(i);

                            DatabaseReference dbref = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        PointOfInterest favPointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                                        if(favPointOfInterest.getCategory().equals(category)){
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                    .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(favPointOfInterest.getCategory(), true))));
                                            mMarkers.put(marker, favPointOfInterest.getKey());
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
                mMarkers = new HashMap<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        for(DataSnapshot childOfChild : child.getChildren()){
                            Marker marker;
                            PointOfInterest pointOfInterest = childOfChild.getValue(PointOfInterest.class);

                            if(mFavLocationsString!=null && mFavLocationsString.size() > 0 && mFavLocationsString.contains(pointOfInterest.getKey())){
                                marker= mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), true))));
                            }else{
                                marker= mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), false))));
                            }

                            mMarkers.put(marker, pointOfInterest.getKey());
                        }

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
        DatabaseReference allLocationsReference = FirebaseDatabase.getInstance().getReference().child("POIs").child(category);
        allLocationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMarkers.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Marker marker;
                        PointOfInterest pointOfInterest = child.getValue(PointOfInterest.class);
                            if(mFavLocationsString.contains(pointOfInterest.getKey())){
                                marker= mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), true))));
                            }else{
                                marker= mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), false))));
                            }
                            // check if the pois is favorite or not
                            mMarkers.put(marker, pointOfInterest.getKey());
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
    public static List<String> getmFavLocationsString() {
        return mFavLocationsString;
    }

    public static void setmFavLocationsString(List<String> mFavLocationsString) {
        MapFragment.mFavLocationsString = mFavLocationsString;
    }

    private static int getSpecificMarker(final String category,final boolean fav){
        int categoryMarker;

        if(fav){
            if(category.equals("Bistro"))
                categoryMarker = R.drawable.f_restaurant;
             else if (category.equals("Pub"))
                categoryMarker = R.drawable.f_restaurant;
            else if (category.equals("Park"))
                categoryMarker = R.drawable.f_park;
            else if (category.equals("Theatre"))
                categoryMarker = R.drawable.f_theatre;
            else if (category.equals("Sport"))
                categoryMarker = R.drawable.f_stadium;
            else if (category.equals("Parking"))
                categoryMarker = R.drawable.f_parking;
            else if (category.equals("Pub"))
                categoryMarker = R.drawable.f_restaurant;
            else if (category.equals("Festival"))
                categoryMarker = R.drawable.f_festival;
            else if (category.equals("Hotel"))
                categoryMarker = R.drawable.f_hotel;
            else if (category.equals("Cinema"))
                categoryMarker = R.drawable.f_cinema;
            else
                categoryMarker = R.drawable.you_marker;

        } else {
            if(category.equals("Bistro"))
                categoryMarker = R.drawable.restaurant;
            else if (category.equals("Pub"))
                categoryMarker = R.drawable.restaurant;
            else if (category.equals("Restaurant"))
                categoryMarker = R.drawable.restaurant;
            else if (category.equals("Park"))
                categoryMarker = R.drawable.park;
            else if (category.equals("Theatre"))
                categoryMarker = R.drawable.theatre;
            else if (category.equals("Sport"))
                categoryMarker = R.drawable.stadium;
            else if (category.equals("Parking"))
                categoryMarker = R.drawable.parking;
            else if (category.equals("Pub"))
                categoryMarker = R.drawable.restaurant;
            else if (category.equals("Festival"))
                categoryMarker = R.drawable.festival;
            else if (category.equals("Hotel"))
                categoryMarker = R.drawable.hotel;
            else if (category.equals("Cinema"))
                categoryMarker = R.drawable.cinema;
            else
                categoryMarker = R.drawable.you_marker;
        }


        return categoryMarker;
    }

    public static void createRoute(LatLng locationLatLng){
        mMap.clear();
        routeCreated = true;
        me = mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.latitude, myLocation.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
        Marker destination = mMap.addMarker(new MarkerOptions().position(new LatLng(locationLatLng.latitude, locationLatLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        String url = getRequestUrl(myLocation, locationLatLng);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);


    }

    private static String getRequestUrl(LatLng origin, LatLng destination) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_des = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_des + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;

        return url;
    }

    private static String requestDirection(String reqUrl) throws IOException {
        String  responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            // Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }

    public static class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try{
                responseString = requestDirection(strings[0]);
            }catch(IOException e){
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);

        }
    }
    public static class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;

            try {

                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            // Get list route and display it in map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));


                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(R.color.colorPrimary);
                polylineOptions.geodesic(true);

            }
            if(polylineOptions!=null){

                mMap.addPolyline(polylineOptions);

            }else
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }
}




