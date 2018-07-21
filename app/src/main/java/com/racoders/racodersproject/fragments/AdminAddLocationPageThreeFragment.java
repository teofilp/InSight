package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.racoders.racodersproject.R;

public class AdminAddLocationPageThreeFragment extends Fragment {

    private EditText description;
    private ImageView locationImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.admin_add_location_page_three_fragment, container, false);

        description = view.findViewById(R.id.description);
        locationImage = view.findViewById(R.id.locationImage);

        locationImage.setImageDrawable(getResources().getDrawable(R.drawable.noimage));



        return view;

    }
}
