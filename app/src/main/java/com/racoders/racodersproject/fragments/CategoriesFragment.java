package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.Category;
import com.racoders.racodersproject.classes.CategoryListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    GridView categoriesGridView;
    List<Category> list;
    public CategoriesFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.categories_fragment, container, false);

        categoriesGridView = view.findViewById(R.id.categoryGrid);
        list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("POIs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Category category = new Category();
                        category.setName(child.getKey());
                        category.setImage(Category.getImageFromName(category.getName()));
                        list.add(category);
                    }
                    categoriesGridView.setAdapter(new CategoryListAdapter(getContext(), list));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }
}
