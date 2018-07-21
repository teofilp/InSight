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

    private EditText name;
    private EditText category;
    private EditText address;
    private EditText phone;
    private EditText email;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_add_location_page_one_fragment, container, false);

        name = view.findViewById(R.id.locationName);
        category = view.findViewById(R.id.locationCategory);
        address = view.findViewById(R.id.locationAddress);
        phone = view.findViewById(R.id.locationPhone);
        email = view.findViewById(R.id.locationEmail);



        return view;
    }
}
