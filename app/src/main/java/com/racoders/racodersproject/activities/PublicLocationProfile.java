package com.racoders.racodersproject.activities;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.racoders.racodersproject.classes.NewsCustomAdapter;
import com.racoders.racodersproject.classes.PointOfInterest;
import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.AdminNews;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PublicLocationProfile extends AppCompatActivity {

    private CircleImageView profile_photo;
    private TextView profileName;
    private TextView category;
    private TextView location;
    private TextView followersNumber;
    private TextView viewsNumber;
    private TextView phoneNumber;
    private TextView websiteAddress;
    private TextView locationDescription;
    private RelativeLayout about_layout;


    private NewsCustomAdapter adapter;
    private RecyclerView profile_posts_recyclerView;

    private PointOfInterest pointOfInterest;

    private int activeScreen = 0;
    private long viewsSum = 0;
    private long followersSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_location_profile);

        initializeView();
        profile_posts_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        profile_posts_recyclerView.setNestedScrollingEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.AdminBlue));
        }

        final String id = getIntent().getStringExtra("id");
        getCurrentLocationNews(id);

        DatabaseReference getPOisDBRef = FirebaseDatabase.getInstance().getReference().child("POIs");
        getPOisDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dsChilds : dataSnapshot.getChildren())
                        for(DataSnapshot childsOfdsChilds : dsChilds.getChildren())
                            if(childsOfdsChilds.getKey().equals(id)){
                                pointOfInterest = childsOfdsChilds.getValue(PointOfInterest.class);
                                updateInfo(pointOfInterest, id);
                            }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void goBack(View view){
        onBackPressed();
    }

    public void toggleScreen(View view){
        String tag = (String)view.getTag();

        Button b1 = findViewById(R.id.posts_button);
        Button b2 = findViewById(R.id.about_button);

        if(tag.equals(Integer.toString(activeScreen))){

        }else{
            if(view.getId() == R.id.about_button){
                profile_posts_recyclerView.animate().translationXBy(-1200).setDuration(300);
                profile_posts_recyclerView.setAdapter(null);
                about_layout.animate().translationXBy(-1200).setDuration(300);
                b2.setTextColor(getResources().getColor(R.color.white));
                b2.setBackgroundColor(getResources().getColor(R.color.CustomTabLayoutActive));
                b1.setTextColor(getResources().getColor(R.color.AdminBlue));
                b1.setBackgroundColor(getResources().getColor(R.color.white));
                activeScreen = 1;

            }else{
                about_layout.animate().translationXBy(1200).setDuration(300);
                profile_posts_recyclerView.setAdapter(adapter);
                profile_posts_recyclerView.animate().translationXBy(1200).setDuration(300);
                b1.setTextColor(getResources().getColor(R.color.white));
                b1.setBackgroundColor(getResources().getColor(R.color.CustomTabLayoutActive));
                b2.setTextColor(getResources().getColor(R.color.AdminBlue));
                b2.setBackgroundColor(getResources().getColor(R.color.white));
                activeScreen = 0;

            }
        }
    }
    private void getCurrentLocationNews(String id){
        final List<News> mList = new ArrayList<>();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News").child(id);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren())
                        mList.add(child.getValue(News.class));

                    adapter = new NewsCustomAdapter(mList, getResources().getColor(R.color.CustomTabLayoutActive));
                    profile_posts_recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void updateInfo(PointOfInterest pointOfInterest, String id){
        updateProfileImage(id);
        profileName.setText(pointOfInterest.getTitle());
        category.setText(pointOfInterest.getCategory());
        location.setText(pointOfInterest.getAdress());
        setLocationDescriptionTextView(pointOfInterest.getDescription());
        setViewsNumberTextView(id);
        setFollowersNumberTextView(pointOfInterest.getKey());
        phoneNumber.setText(pointOfInterest.getPhoneNumber());


    }

    private void updateProfileImage(String id){
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + id + ".jpeg");
        GlideApp.with(getApplicationContext()).load(storage).into(profile_photo);
    }

    private void setViewsNumberTextView(String id){
        FirebaseDatabase.getInstance().getReference().child("News").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot children : dataSnapshot.getChildren()){
                            viewsSum+= children.getValue(News.class).getViewsNumber();
                        }
                        String viewsNumberString = "<b>" + viewsSum + "</b> views";
                        Spanned sp = Html.fromHtml(viewsNumberString);
                        viewsNumber.setText(sp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void setFollowersNumberTextView(final String key){
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

                            String followersNumberString = "<b>" + followersSum + "</b> followers";
                            Spanned sp = Html.fromHtml(followersNumberString);
                            followersNumber.setText(sp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setLocationDescriptionTextView(String value){
        Spanned sp = Html.fromHtml("<h4>Descriere</h4>" + value);
        locationDescription.setText(sp);
    }

    private void initializeView(){
        profile_photo = findViewById(R.id.public_profile_image);
        profileName = findViewById(R.id.profileName);
        category = findViewById(R.id.category);
        location = findViewById(R.id.location);
        profile_posts_recyclerView = findViewById(R.id.profile_posts_recyclerView);
        about_layout = findViewById(R.id.about_layout);
        followersNumber = findViewById(R.id.followersNumbers);
        viewsNumber = findViewById(R.id.viewsNumber);
        phoneNumber = findViewById(R.id.phoneNumber);
        websiteAddress = findViewById(R.id.websiteAddress);
        locationDescription = findViewById(R.id.locationDescription);

        about_layout.animate().translationXBy(1200);
    }

}
