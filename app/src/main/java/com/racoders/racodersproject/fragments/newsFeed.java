package com.racoders.racodersproject.fragments;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.racoders.racodersproject.classes.NewsCustomAdapter;

import java.util.ArrayList;
import java.util.List;

public class newsFeed extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private int color;

    public newsFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_feed, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        color = getActivity().getResources().getColor(R.color.white);
        final ArrayList<News> mArrayList = new ArrayList<>();


//        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News");
//        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mArrayList.clear();
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    for(DataSnapshot child : ds.getChildren()){
//                        News news = child.getValue(News.class);
//                        mArrayList.add(news);
//                    }
//                }
//                NewsCustomAdapter adapter = new NewsCustomAdapter(mArrayList, color);
//                recyclerView.setAdapter(adapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        System.out.println("array size: "+ mArrayList.size());
        populateRecyclerViewWithFavoriteNews();

        return view;
    }

    private void populateRecyclerViewWithFavoriteNews(){

        FirebaseDatabase.getInstance().getReference().child("FavoriteLocations").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>(){};
                        List<String> list = dataSnapshot.getValue(t);

                        loadIntoRecyclerView(list);

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
                                    for(DataSnapshot child : dataSnapshot.getChildren()){
                                        myList.add(child.getValue(News.class));
                                    }
                                    NewsCustomAdapter adapter = new NewsCustomAdapter(myList, getResources().getColor(R.color.white));
                                    recyclerView.setAdapter(adapter);
                                }
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
