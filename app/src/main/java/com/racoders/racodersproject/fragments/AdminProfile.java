package com.racoders.racodersproject.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.racoders.racodersproject.classes.PointOfInterest;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminProfile extends Fragment implements OnMapReadyCallback {

    private static CircleImageView profileImage;
    private TextView title;
    private static TextView description;
    private RatingBar ratingBar;
    private TextView viewsNumber;
    private TextView followersNumber;
    private static TextView postsNumber;
    private TextView locationPhone;
    private TextView locationEmail;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private PointOfInterest me;

    private int viewsSum;
    private int followersSum;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_profile, container, false);
        profileImage = view.findViewById(R.id.locationProfileImage);
        title = view.findViewById(R.id.locationName);
        description = view.findViewById(R.id.locationDescription);
        ratingBar = view.findViewById(R.id.RatingBar);
        viewsNumber = view.findViewById(R.id.viewsNumber);
        followersNumber = view.findViewById(R.id.followersNumbers);
        postsNumber = view.findViewById(R.id.postsNumber);
        locationPhone = view.findViewById(R.id.locationPhone);
        locationEmail = view.findViewById(R.id.locationEmail);

        viewsSum = 0;
        followersSum = 0;

//        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.profileMap);
//        if(mGoogleMap == null){
//            mapFragment.getMapAsync(this);
//        }
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profileImage);

        setAdminData();
        setViewsNumberTextView(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setPostsNumber(FirebaseAuth.getInstance().getCurrentUser().getUid());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;



    }

    @Override
    public void onStart() {
        super.onStart();

        if(mGoogleMap!=null){
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshotChild : dataSnapshot.getChildren())
                            for(DataSnapshot childOfChild : snapshotChild.getChildren())
                                if(childOfChild.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    me = childOfChild.getValue(PointOfInterest.class);
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(me.getLatitude(), me.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));

                                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(me.getLatitude(), me.getLongitude()), 15));
                                    title.setText(me.getTitle());
                                    Spanned sp = Html.fromHtml(me.getDescription());
                                    description.setText(sp);
                                    ratingBar.setRating(me.getRatingSum()/me.getRatingNumb());

                                }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    public static TextView getPostsNumber(){
        return postsNumber;
    }

    public static CircleImageView getProfileImage() { return profileImage; }

    public static TextView getDescription() { return  description; }

    private void setViewsNumberTextView(String id){
        FirebaseDatabase.getInstance().getReference().child("News").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot children : dataSnapshot.getChildren()){
                            viewsSum+= children.getValue(News.class).getViewsNumber();
                        }

                        viewsNumber.setText(Integer.toString(viewsSum));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setFollowersNumberTextView(final String key){
        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot children : dataSnapshot.getChildren()){
                                GenericTypeIndicator<ArrayList<String> > t = new GenericTypeIndicator<ArrayList<String> >(){};
                                ArrayList<String> usersFav = children.getValue(t);

                                for(String str : usersFav)
                                    if(str.equals(key))
                                        followersSum++;
                            }

                            followersNumber.setText(Integer.toString(followersSum));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setPostsNumber(String id){
        final int sum = 0;
        FirebaseDatabase.getInstance().getReference().child("News").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    postsNumber.setText(Integer.toString((int)dataSnapshot.getChildrenCount()));
                } else {
                    postsNumber.setText(Integer.toString(sum));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdminData(){
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshotChild : dataSnapshot.getChildren())
                        for(DataSnapshot childOfChild : snapshotChild.getChildren())
                            if(childOfChild.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                me = childOfChild.getValue(PointOfInterest.class);
//                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(me.getLatitude(), me.getLongitude()))
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));

//                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(me.getLatitude(), me.getLongitude()), 15));
                                setFollowersNumberTextView(me.getKey());
                                locationEmail.setText(me.getEmailAddress());
                                locationPhone.setText(me.getPhoneNumber());
                                title.setText(me.getTitle());
                                Spanned sp = Html.fromHtml(me.getDescription());
                                description.setText(sp);
                                ratingBar.setRating(me.getRatingSum()/me.getRatingNumb());

                            }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
