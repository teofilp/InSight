package com.racoders.racodersproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.PointOfInterest;
import com.racoders.racodersproject.classes.ViewPagerAdapter;
import com.racoders.racodersproject.fragments.AdminAddLocationPageOneFragment;
import com.racoders.racodersproject.fragments.AdminAddLocationPageThreeFragment;
import com.racoders.racodersproject.fragments.AdminAddLocationPageTwoFragment;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.facebook.FacebookSdk.getApplicationContext;


public class AddLocation extends AppCompatActivity{

    private Button nt_button;
    private Button bk_button;
    private View[] dots;
    private int currTab = 0;
    private static Uri locationImageUri;
    public static LatLng locationLatLng;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private String locationEmailAuth;
    private String locationPasswordAuth;
    private String locationDisplayName;

    AdminAddLocationPageOneFragment fragment1;


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
        else if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                AdminAddLocationPageThreeFragment.locationImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Crop.pickImage(AddLocation.this);
                    }
                });
            }else
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.AdminBlue));
        }

        locationEmailAuth = getIntent().getStringExtra("email");
        locationPasswordAuth = getIntent().getStringExtra("password");
        locationDisplayName = getIntent().getStringExtra("displayName");

        fragment1 = new AdminAddLocationPageOneFragment();
        handleInfoForAddLocation(locationEmailAuth, locationDisplayName, fragment1);

        nt_button = findViewById(R.id.nt_button);
        bk_button = findViewById(R.id.bk_button);
        dots = new View[3];
        dots[0] = findViewById(R.id.firstPageDot);
        dots[1] = findViewById(R.id.secondPageDot);
        dots[2] = findViewById(R.id.thirdPageDot);

        viewPager = findViewById(R.id.addLocationViewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(fragment1);
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
                    currTab = 0;
                }else if(position ==1){
                    bk_button.setVisibility(View.VISIBLE);
                    dots[0].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[1].setBackground(getResources().getDrawable(R.drawable.round_dots));
                    dots[2].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    nt_button.setText("Next");
                    currTab = 1;
                }else{
                    dots[0].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[1].setBackground(getResources().getDrawable(R.drawable.inactive_dots));
                    dots[2].setBackground(getResources().getDrawable(R.drawable.round_dots));
                    nt_button.setText("Save");
                    currTab = 2;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(pagerAdapter);
        Toast.makeText(this, Integer.toString(currTab), Toast.LENGTH_SHORT).show();
        bk_button.setVisibility(View.GONE);
        nt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() == 2)
                    saveLocation();
                if(currTab < 2)
                    viewPager.setCurrentItem(++currTab);

            }
        });
        bk_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currTab>0)
                    viewPager.setCurrentItem(--currTab);
            }
        });

    }

    private void handleInfoForAddLocation(String locationEmailAuth, String locationDisplayName, AdminAddLocationPageOneFragment fragment) {

        Bundle bundle = new Bundle();
        bundle.putString("email", locationEmailAuth);
        bundle.putString("displayName", locationDisplayName);

        fragment.setArguments(bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri source_uri = data.getData();
                Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Crop.of(source_uri, destination_uri).asSquare().start(this);
                AdminAddLocationPageThreeFragment.setImageUri(Crop.getOutput(data));

            }
            else if (requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode, data);
            }
        }
    }
    public void handle_crop(int code, Intent data){

        if(code == RESULT_OK){
            AdminAddLocationPageThreeFragment.setImageUri(Crop.getOutput(data));
        }
        else if (code == Crop.RESULT_ERROR){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveLocation(){
        AdminAddLocationPageThreeFragment.progressBar.setVisibility(View.VISIBLE);
        final PointOfInterest mPoinOfInterest = new PointOfInterest();

        mPoinOfInterest.setTitle(AdminAddLocationPageOneFragment.getLocationName());
        mPoinOfInterest.setCategory(AdminAddLocationPageOneFragment.getLocationCategory());
        mPoinOfInterest.setEmailAddress(AdminAddLocationPageOneFragment.getLocationEmail());
        mPoinOfInterest.setPhoneNumber(AdminAddLocationPageOneFragment.getLocationPhone());
        mPoinOfInterest.setAdress(AdminAddLocationPageTwoFragment.getLocationAddress());
        mPoinOfInterest.setDescription(AdminAddLocationPageThreeFragment.getLocationDescription());
        mPoinOfInterest.setRatingNumb(0);
        mPoinOfInterest.setRatingSum(0);
        if(locationLatLng!=null){
            mPoinOfInterest.setLatitude(locationLatLng.latitude);
            mPoinOfInterest.setLongitude(locationLatLng.longitude);
            locationLatLng = null;
        }else{
            Toast.makeText(this, "Your location on the map is required", Toast.LENGTH_SHORT).show();
            AdminAddLocationPageThreeFragment.progressBar.setVisibility(View.GONE);
            viewPager.setCurrentItem(1, true);
            return;
        }
        if(mPoinOfInterest.getTitle().equals("") || mPoinOfInterest.getCategory().equals("")){
            Toast.makeText(this, "All fields with * are required, including " +
                    "your location on map", Toast.LENGTH_LONG).show();
            AdminAddLocationPageThreeFragment.progressBar.setVisibility(View.GONE);
            return;
        }


        final ImageView mPointOfInterestImageView = AdminAddLocationPageThreeFragment.getLocationImage();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(locationEmailAuth, locationPasswordAuth).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                saveInfo(mPoinOfInterest, mPointOfInterestImageView);
            }
        });



    }

    private void saveInfo(final PointOfInterest mPoinOfInterest, final ImageView mPointOfInterestImageView){

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs").child(mPoinOfInterest.getCategory())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String value = FirebaseDatabase.getInstance().getReference().child("POIs").child(mPoinOfInterest.getCategory())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).toString();

        mPoinOfInterest.setKey(value);

        dbref.setValue(mPoinOfInterest, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference spaceref = storage.getReference().child("images/pois/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");

                    mPointOfInterestImageView.setDrawingCacheEnabled(true);
                    mPointOfInterestImageView.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) mPointOfInterestImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = spaceref.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Image upload failed");
                            AdminAddLocationPageThreeFragment.progressBar.setVisibility(View.GONE);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            AdminAddLocationPageThreeFragment.progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), AdminPanel.class));
                            clearMemory();
                            finish();

                        }
                    });

                }
            }
        });
    }

    private void clearMemory(){
        AdminAddLocationPageTwoFragment.mMap = null;
        AdminAddLocationPageTwoFragment.setLocationLatLng(null);
    }


    public static Uri getLocationImageUri(){
        return locationImageUri;
    }

    public static void setLocationImageUri(Uri uri){
        locationImageUri = uri;
    }


}
