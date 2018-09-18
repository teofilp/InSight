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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.MarkerDetailsPopUpWindow;
import com.racoders.racodersproject.classes.DirectionsParser;
import com.racoders.racodersproject.classes.DistanceCalculator;
import com.racoders.racodersproject.classes.PointOfInterest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class UserMapFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    SupportMapFragment mapFragment;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    public static LatLng myLocation;
    public static RelativeLayout imageButton;
    private int contor=0;
    public static Marker me;
    public static HashMap<Marker, String> mMarkers = new HashMap<>();
    public static boolean isFavOnly = true;

    private static List<String> mFavLocationsString;
    private static Spinner myFilters;
    public static String activeFilter;
    private static String str;
    public static HashMap<String, PointOfInterest> mFavPOIs = new HashMap<>();
    private static boolean routeCreated = false;
    private static RelativeLayout routePopUp;
    private static CircleImageView routeProfile;
    private static TextView routeTitle;
    private static TextView routeDistance;
    private ImageView cancelRouteButton;


    private static View animationRadioButton;
    private static TextView allTextView;
    private static TextView favTextView;
    private static RelativeLayout radioWrapper;
    private static RelativeLayout spinnerWrapper;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_map_fragment, container, false);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_fragment1);
        if(mMap == null)
            mapFragment.getMapAsync(this);
        activeFilter = "All";
        imageButton = view.findViewById(R.id.locationTrackerButton);

        imageButton.setEnabled(false);
        myFilters = view.findViewById(R.id.filter_spinner);
        routePopUp = view.findViewById(R.id.routePopUp);
        routeProfile = view.findViewById(R.id.route_profile);
        routeTitle = view.findViewById(R.id.route_title);
        routeDistance = view.findViewById(R.id.routeDistance);
        cancelRouteButton = view.findViewById(R.id.cancel_route);

        allTextView = view.findViewById(R.id.all);
        favTextView = view.findViewById(R.id.favorite);
        animationRadioButton = view.findViewById(R.id.animationRadioButton);
        radioWrapper = view.findViewById(R.id.radioWrapper);
        spinnerWrapper = view.findViewById(R.id.spinnerWrapper);

        cancelRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRoute();
            }
        });
//        checkForNewUser(FirebaseAuth.getInstance().getUid());
        routePopUp.setVisibility(View.GONE);


        return view;

    }

    private void cancelRoute() {
        routePopUp.animate().translationY(480).setDuration(400);
        routePopUp.setVisibility(View.GONE);
        makeViewsVisible();
        routeCreated = false;
        onResume();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(me)) {
                    String markerId = mMarkers.get(marker);
                    startActivity(new Intent(getApplicationContext(), MarkerDetailsPopUpWindow.class).putExtra("id", markerId));
                } else {
                    Toast.makeText(getContext(), "This is your location", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.user_map_style));
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

                if(!imageButton.isEnabled()){
                    imageButton.setEnabled(true);
                }
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(mMap!=null){
                    if(me!=null)
                        me.remove();
                    me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
                    if(contor==0){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, locationListener);
        }

        dealWithSpinner();
    }

    private void dealWithSpinner() {

        final List<String> categoriesList = new ArrayList<>();
        categoriesList.add("All");
        FirebaseDatabase.getInstance().getReference().child("POIs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        categoriesList.add(child.getKey());
                    }
                    ArrayAdapter<String> myFilterAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, categoriesList);
                    myFilterAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    myFilters.setAdapter(myFilterAdapter);

                    myFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            activeFilter = categoriesList.get(position);
                            reloadMap();
                            if(isFavOnly){
                                if(activeFilter.equals("All"))
                                    getFavPOIS();
                                else
                                    getFavPOIS(activeFilter);
                            } else{
                                if(activeFilter.equals("All"))
                                    getAllPOIS();
                                else
                                    getAllPOIS(activeFilter);
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    myFilters.setSelection(myFilterAdapter.getPosition(activeFilter));
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
        if(mMap == null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMap!=null && myLocation!=null && !routeCreated){
            mMap.clear();
            dealWithSpinner();
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, locationListener);
            }
            if(myLocation!=null){
                me = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
            }

        } else{
            onStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMap!=null)
            mMap.clear();
        routeCreated = false;
        locationManager.removeUpdates(locationListener);
    }

    public static void getFavPOIS(){
        mMarkers.clear();
        final DatabaseReference favDbRef = FirebaseDatabase.getInstance().getReference()
                .child("FavoriteLocations").child(FirebaseAuth.getInstance().getUid());
        favDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                    mFavLocationsString = dataSnapshot.getValue(t);
                    for(int i = 0; i< mFavLocationsString.size(); i++){

                        str = mFavLocationsString.get(i);

                        if(str!=null){
                            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReferenceFromUrl(str);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        PointOfInterest favPointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                                        if(mMap!=null){
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                    .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(favPointOfInterest.getCategory(), true))));
                                            mMarkers.put(marker, favPointOfInterest.getKey());
                                            mFavPOIs.put(dataSnapshot.getKey(), favPointOfInterest);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void getFavPOIS(final String category){
        mMarkers.clear();
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
                                        if(favPointOfInterest.getCategory().equals("All"))
                                            getFavPOIS();
                                        else if (favPointOfInterest.getCategory().equals(category) && mMap != null){
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(favPointOfInterest.getLatitude(), favPointOfInterest.getLongitude()))
                                                    .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(favPointOfInterest.getCategory(), true))));
                                            mMarkers.put(marker, favPointOfInterest.getKey());
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static void getAllPOIS(){
        mMarkers = new HashMap<>();
        DatabaseReference allLocationsReference = FirebaseDatabase.getInstance().getReference().child("POIs");
        allLocationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        for(DataSnapshot childOfChild : child.getChildren()){
                            Marker marker;
                            PointOfInterest pointOfInterest = childOfChild.getValue(PointOfInterest.class);

                            if(mFavLocationsString!=null && mFavLocationsString.size() > 0 && mFavLocationsString.contains(pointOfInterest.getKey()) && mMap != null){
                                marker= mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), true))));
                                mMarkers.put(marker, pointOfInterest.getKey());
                            }else{
                                if(mMap != null){
                                    marker= mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(pointOfInterest.getLatitude(), pointOfInterest.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(getSpecificMarker(pointOfInterest.getCategory(), false))));
                                    mMarkers.put(marker, pointOfInterest.getKey());
                                }

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

    public static View getAnimationRadioButton() {
        return animationRadioButton;
    }

    public static TextView getAllTextView() {
        return allTextView;
    }

    public static TextView getFavTextView() {
        return favTextView;
    }

    public static void getAllPOIS(final String category){
        mMarkers = new HashMap<>();
        DatabaseReference allLocationsReference = FirebaseDatabase.getInstance().getReference().child("POIs").child(category);
        allLocationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Marker marker;
                        PointOfInterest pointOfInterest = child.getValue(PointOfInterest.class);
                        if(mMap != null){
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
                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void reloadMap(){
        if(mMap != null)
            mMap.clear();
        if(myLocation!=null){
            me = mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));
        }
    }
    public static List<String> getmFavLocationsString() {
        return mFavLocationsString;
    }

    public static void setmFavLocationsString(List<String> mFavLocationsString) {
        UserMapFragment.mFavLocationsString = mFavLocationsString;
    }

    private static int getSpecificMarker(final String category,final boolean fav){
        int categoryMarker;

        if(fav){
            if(category.equals("Bistro") || category.equals("Restaurant"))
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
                categoryMarker = R.drawable.unknown;

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
                categoryMarker = R.drawable.unknown;
        }


        return categoryMarker;
    }

    public static void createRoute(LatLng locationLatLng){

        if(myLocation != null && mMap != null){
            clearView();
            mMap.clear();
            routeCreated = true;
            me = mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.latitude, myLocation.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.you_marker)));

            Marker destination = mMap.addMarker(new MarkerOptions().position(new LatLng(locationLatLng.latitude, locationLatLng.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            String url = getRequestUrl(myLocation, locationLatLng);
            TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
            taskRequestDirections.execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "Could not create route", Toast.LENGTH_SHORT).show();
            clearView();
            makeViewsVisible();
            routePopUp.setVisibility(View.GONE);
        }

    }

    private static void clearView() {
        spinnerWrapper.setVisibility(View.GONE);
        radioWrapper.setVisibility(View.GONE);
        myFilters.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        animateRoutePopUp();

    }
    private static void makeViewsVisible(){
        spinnerWrapper.setVisibility(View.VISIBLE);
        radioWrapper.setVisibility(View.VISIBLE);
        myFilters.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.VISIBLE);
    }

    private static void animateRoutePopUp() {

        routePopUp.setVisibility(View.VISIBLE);
        routePopUp.animate().translationY(-480).setDuration(600);

    }
    public static void loadRouteInfo(String id, PointOfInterest pointOfInterest){
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + id + ".jpeg");
        com.racoders.racodersproject.AppGlideModule.GlideApp.with(getApplicationContext()).load(storage).into(routeProfile);
        routeTitle.setText(pointOfInterest.getTitle());
        double routeDistanceDouble = DistanceCalculator.distance(myLocation.latitude, myLocation.longitude, pointOfInterest.getLatitude(), pointOfInterest.getLongitude());
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        routeDistance.setText(numberFormat.format(routeDistanceDouble) + " km to destination");


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

    private static class TaskRequestDirections extends AsyncTask<String, Void, String>{

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
    private static class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{

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

            ArrayList<LatLng> points = null;

            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));


                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                polylineOptions.geodesic(true);

            }
            if(polylineOptions!=null && mMap != null){

                mMap.addPolyline(polylineOptions);

            }else
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

        }
    }
}




