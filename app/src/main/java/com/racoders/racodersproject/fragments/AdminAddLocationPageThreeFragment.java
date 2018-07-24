package com.racoders.racodersproject.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.AddLocation;
import com.racoders.racodersproject.activities.AddNews;
import com.soundcloud.android.crop.Crop;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdminAddLocationPageThreeFragment extends Fragment {

    private static EditText locationDescription;
    public static ImageView locationImage;
    public static ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.admin_add_location_page_three_fragment, container, false);

        locationDescription = view.findViewById(R.id.locationDescription);
        locationImage = view.findViewById(R.id.locationImage);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.bringToFront();
        if(AddLocation.getLocationImageUri() == null)
            locationImage.setImageDrawable(getResources().getDrawable(R.drawable.noimage));
        else
            locationImage.setImageURI(AddLocation.getLocationImageUri());
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            locationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "image clicked", Toast.LENGTH_SHORT).show();
                    Crop.pickImage(getActivity());

                }
            });
        else
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        return view;

    }
    public static void setImageUri(Uri uri){
        locationImage.setImageURI(uri);
        AddLocation.setLocationImageUri(uri);
    }

    public static String getLocationDescription(){
        return  locationDescription.getText().toString();
    }

    public static ImageView getLocationImage(){
        return locationImage;
    }
}
