package com.racoders.racodersproject.activities;

import android.animation.Animator;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.racoders.racodersproject.classes.NewsCustomAdapter;
import com.racoders.racodersproject.classes.PointOfInterest;
import com.racoders.racodersproject.classes.Review;
import com.racoders.racodersproject.classes.ReviewsCustomAdapter;
import com.racoders.racodersproject.classes.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicLocationProfile extends AppCompatActivity {

    private CircleImageView profile_photo;
    private ImageView bookmarkImageView;
    private TextView profileName;
    private TextView category;
    private TextView location;
    private TextView followersNumber;
    private TextView viewsNumber;
    private TextView phoneNumber;
    private TextView websiteAddress;
    private TextView locationDescription;
    private TextView usersSavedReview;

    private RatingBar locationRatingBar;
    private RatingBar toAddRatingBar;
    private RatingBar usersRatingBar;

    private RelativeLayout about_layout;
    private LinearLayout reviewLayout;
    private RelativeLayout addReviewLayout;
    private RelativeLayout addReviewRequestLayout;
    private RelativeLayout usersReviewLayout;

    private EditText toAddReviewDescription;

    private ReviewsCustomAdapter reviewsAdapter;
    private RecyclerView reviews_recyclerView;

    private NewsCustomAdapter newsAdapter;
    private RecyclerView profile_posts_recyclerView;
    private List<String> favList;
    private List<String> reviewsAuthors;
    private String id;

    private PointOfInterest pointOfInterest;
    private boolean isFav = false;

    private int activeScreen = 0;
    private long viewsSum = 0;
    private long followersSum = 0;
    private float usersRating;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_location_profile);

        initializeView();
        profile_posts_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        profile_posts_recyclerView.setNestedScrollingEnabled(false);
        reviews_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviews_recyclerView.setNestedScrollingEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        id = getIntent().getStringExtra("id");
        getCurrentLocationNews(id);
        getcurrentLocationReviews(id);

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

    public void toggleFavorite(View view){
        String key = pointOfInterest.getKey();

        if(isFav){
            for(int i=0; i<favList.size(); i++){
                if(key.equals(favList.get(i))){
                    favList.remove(i);
                    break;
                }
            }


        } else {
            favList.add(key);
        }
        isFav = !isFav;
        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(favList, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    if(isFav)
                        bookmarkImageView.setBackground(getResources().getDrawable(R.drawable.favorite_bookmark));
                    else
                        bookmarkImageView.setBackground(getResources().getDrawable(R.drawable.not_favorite));
                }else
                    Toast.makeText(PublicLocationProfile.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toggleScreen(View view){
        String tag = (String)view.getTag();

        Button posts_button = findViewById(R.id.posts_button);
        Button about_button = findViewById(R.id.about_button);
        Button reviews_button = findViewById(R.id.reviews_button);

        if(tag.equals(Integer.toString(activeScreen))){

        }else{
            if(view.getId() == R.id.about_button){

                profile_posts_recyclerView.animate().translationX(-1200).setDuration(300);
                profile_posts_recyclerView.setAdapter(null);
                reviewLayout.animate().translationX(1200).setDuration(300);
                reviews_recyclerView.setAdapter(null);
                about_layout.animate().translationX(0).setDuration(300);
                about_button.setTextColor(getResources().getColor(R.color.white));
                about_button.setBackgroundColor(getResources().getColor(R.color.CustomTabLayoutActive));
                posts_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                posts_button.setBackgroundColor(getResources().getColor(R.color.white));
                reviews_button.setBackgroundColor(getResources().getColor(R.color.white));
                reviews_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                activeScreen = 1;

            }else if (view.getId() == R.id.posts_button){

                about_layout.animate().translationX(1200).setDuration(300);
                reviewLayout.animate().translationX(2400).setDuration(300);
                reviews_recyclerView.setAdapter(null);
                profile_posts_recyclerView.setAdapter(newsAdapter);
                profile_posts_recyclerView.animate().translationX(0).setDuration(300);
                posts_button.setTextColor(getResources().getColor(R.color.white));
                posts_button.setBackgroundColor(getResources().getColor(R.color.CustomTabLayoutActive));
                about_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                about_button.setBackgroundColor(getResources().getColor(R.color.white));
                reviews_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                reviews_button.setBackgroundColor(getResources().getColor(R.color.white));
                activeScreen = 0;

            } else if (view.getId() == R.id.reviews_button){

                reviews_recyclerView.setAdapter(reviewsAdapter);
                reviewLayout.animate().translationX(0).setDuration(300);
                profile_posts_recyclerView.setAdapter(null);
                profile_posts_recyclerView.animate().translationX(-2400);
                about_layout.animate().translationX(-1200).setDuration(300);
                posts_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                posts_button.setBackgroundColor(getResources().getColor(R.color.white));
                about_button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                about_button.setBackgroundColor(getResources().getColor(R.color.white));
                reviews_button.setTextColor(getResources().getColor(R.color.white));
                reviews_button.setBackgroundColor(getResources().getColor(R.color.CustomTabLayoutActive));
                activeScreen = 2;
            }
        }
    }
    public void requestReview(View view){
        addReviewRequestLayout.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                addReviewRequestLayout.setVisibility(View.GONE);
                addReviewLayout.setVisibility(View.VISIBLE);
                addReviewLayout.animate().alpha(1).setDuration(200);
                toAddRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        usersRating = rating;
                        usersRatingBar.setRating(rating);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void saveReview(View view){

        usersSavedReview.setText(toAddReviewDescription.getText().toString());
        FirebaseDatabase.getInstance().getReferenceFromUrl(pointOfInterest.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    pointOfInterest = dataSnapshot.getValue(PointOfInterest.class);
                    pointOfInterest.setRatingNumb(pointOfInterest.getRatingNumb() + 1);
                    pointOfInterest.setRatingSum(pointOfInterest.getRatingSum() + usersRating);

                    FirebaseDatabase.getInstance().getReferenceFromUrl(pointOfInterest.getKey()).setValue(pointOfInterest, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null)
                                Toast.makeText(PublicLocationProfile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            else
                                new Review(User.getCurrentUserDisplayName(FirebaseAuth.getInstance().getCurrentUser().getUid()),
                                    toAddReviewDescription.getText().toString(), usersRating, Calendar.getInstance().getTime()).save(id, addReviewLayout, usersReviewLayout);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForGivenReview(List<Review> mList) {
        if(reviewsAuthors.contains(User.getCurrentUserDisplayName(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
            addReviewRequestLayout.setVisibility(View.GONE);
            usersReviewLayout.setVisibility(View.VISIBLE);
            for(Review review : mList){
                if(review.getAuthor().equals(User.getCurrentUserDisplayName(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                    usersRatingBar.setRating((float)review.getRating());
                    usersSavedReview.setText(review.getDescription());
                    usersRatingBar.setOnRatingBarChangeListener(null);
                    break;
                }
            }
        }

    }

    private void getcurrentLocationReviews(String id) {
        final List<Review> mList = new ArrayList<>();
        reviewsAuthors = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Reviews")
                .child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Review review = child.getValue(Review.class);
                        mList.add(review);
                        reviewsAuthors.add(mList.get(mList.size()-1).getAuthor());
                    }

                    reviewsAdapter = new ReviewsCustomAdapter(mList);
                    reviews_recyclerView.setAdapter(reviewsAdapter);
                    checkForGivenReview(mList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                    newsAdapter = new NewsCustomAdapter(mList, getResources().getColor(R.color.CustomTabLayoutActive));
                    profile_posts_recyclerView.setAdapter(newsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void updateInfo(PointOfInterest pointOfInterest, String id){
        updateProfileImage(id);
        checkIfFavoriteAndAddBookmark(pointOfInterest.getKey());
        profileName.setText(pointOfInterest.getTitle());
        category.setText(pointOfInterest.getCategory());
        location.setText(pointOfInterest.getAdress());
        setLocationDescriptionTextView(pointOfInterest.getDescription());
        setViewsNumberTextView(id);
        setFollowersNumberTextView(pointOfInterest.getKey());
        locationRatingBar.setRating(pointOfInterest.getRatingSum() / pointOfInterest.getRatingNumb());
        phoneNumber.setText(pointOfInterest.getPhoneNumber());

    }

    private void checkIfFavoriteAndAddBookmark(final String key) {
        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator< List<String>> t = new GenericTypeIndicator< List<String>>(){};
                    favList = dataSnapshot.getValue(t);

                    for(String str : favList)
                        if(str.equals(key)){
                            isFav = true;
                            break;
                        }

                    if(!isFav){
                        bookmarkImageView.setBackground(getResources().getDrawable(R.drawable.not_favorite));
                    } else {
                        bookmarkImageView.setBackground(getResources().getDrawable(R.drawable.favorite_bookmark));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        bookmarkImageView = findViewById(R.id.bookmarkImageView);
        locationRatingBar = findViewById(R.id.locationRating);
        reviews_recyclerView = findViewById(R.id.reviews_recyclerView);
        reviewLayout = findViewById(R.id.reviewLayout);
        addReviewLayout = findViewById(R.id.addReviewLayout);
        addReviewRequestLayout = findViewById(R.id.addReviewRequestLayout);
        toAddRatingBar = findViewById(R.id.toAddRatingBar);
        toAddReviewDescription = findViewById(R.id.toAddReviewDescription);
        usersReviewLayout = findViewById(R.id.usersReviewLayout);
        usersRatingBar = findViewById(R.id.usersRating);
        usersSavedReview = findViewById(R.id.usersSavedReview);

        about_layout.animate().translationX(1200).setDuration(0);
        reviewLayout.animate().translationX(2400).setDuration(0);
        addReviewLayout.setAlpha(0);
        addReviewLayout.setVisibility(View.GONE);
        usersReviewLayout.setVisibility(View.GONE);

    }


}
