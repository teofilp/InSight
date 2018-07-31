package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.racoders.racodersproject.classes.NewsCustomAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminNews extends Fragment {

    private static RecyclerView recyclerView;
    private static ArrayList<News> mList = new ArrayList<>();
    private static NewsCustomAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.admin_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mList.clear();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for(DataSnapshot child : dataSnapshot.getChildren())
                        mList.add(child.getValue(News.class));

                adapter = new NewsCustomAdapter(mList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
    public static ArrayList<News> getmList(){
        return  mList;
    }
    public static void setAdapter(ArrayList<News> list){
        adapter = new NewsCustomAdapter(list);
        recyclerView.setAdapter(adapter);
    }
    public  static NewsCustomAdapter getAdapter(){
        return adapter;

    }
    public static void loadRecyclerView(){
        mList.clear();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for(DataSnapshot child : dataSnapshot.getChildren())
                        mList.add(child.getValue(News.class));

                adapter = new NewsCustomAdapter(mList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadRecyclerView();

    }
}
