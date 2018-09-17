package com.racoders.racodersproject.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.racoders.racodersproject.classes.NewsCustomAdapter;
import com.racoders.racodersproject.classes.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserNewsfeedFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static UserNewsfeedFragment instance;

    public UserNewsfeedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_newsfeed_fragment, container, false);

        instance = this;

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        populateRecyclerViewWithFavoriteNews();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
                populateRecyclerViewWithFavoriteNews();
            }
        });

        return view;
    }

    public static UserNewsfeedFragment getInstance() { return instance; }

    public void populateRecyclerViewWithFavoriteNews(){

        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                        List<String> list = dataSnapshot.getValue(t);

                        if(list!= null){
                            Collections.reverse(list);
                            loadIntoRecyclerView(list);
                        } else {
                            recyclerView.setAdapter(null);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadIntoRecyclerView(List<String> list) {
        final List<News> myList = new ArrayList<>();
        if(list!=null)
        for(String str : list){
            FirebaseDatabase.getInstance().getReferenceFromUrl(str).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String key = dataSnapshot.getKey();

                        FirebaseDatabase.getInstance().getReference().child("News").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    view.findViewById(R.id.warningText).setVisibility(View.GONE);

                                    for(DataSnapshot child : dataSnapshot.getChildren())
                                        myList.add(child.getValue(News.class));

                                } else {
                                    myList.clear();
                                    view.findViewById(R.id.warningText).setVisibility(View.VISIBLE);
                                }

                                NewsCustomAdapter adapter = new NewsCustomAdapter(myList, getResources().getColor(R.color.white));

                                recyclerView.setAdapter(adapter);

                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
