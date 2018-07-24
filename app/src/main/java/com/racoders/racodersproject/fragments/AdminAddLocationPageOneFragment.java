package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.racoders.racodersproject.R;

public class AdminAddLocationPageOneFragment extends Fragment {

    private static EditText locationName;
    private static EditText locationCategory;
    private static EditText locationPhone;
    private static EditText locationEmail;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_add_location_page_one_fragment, container, false);

        locationName = view.findViewById(R.id.locationName);
        locationCategory = view.findViewById(R.id.locationCategory);
        locationPhone = view.findViewById(R.id.locationPhone);
        locationEmail = view.findViewById(R.id.locationEmail);

        return view;
    }

    public static String getLocationName(){
        return locationName.getText().toString();
    }

    public static String getLocationCategory(){
        return locationCategory.getText().toString();
    }

    public static String getLocationPhone(){
        return locationPhone.getText().toString();
    }

    public static String getLocationEmail(){
        return locationEmail.getText().toString();
    }

}
