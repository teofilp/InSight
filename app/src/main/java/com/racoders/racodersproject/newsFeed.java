package com.racoders.racodersproject;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class newsFeed extends Fragment {

    View view;
    public newsFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_feed, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = FirebaseDatabase.getInstance().getReference().child("News");

        FirebaseRecyclerOptions<News> options = new FirebaseRecyclerOptions.Builder<News>()
                .setQuery(query, News.class)
                .build();
        FirebaseRecyclerAdapter<News, NewsHolder> adapter = new FirebaseRecyclerAdapter<News, NewsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NewsHolder holder, int position, @NonNull News model) {
                    holder.setTitle(model.getTitle());
                    holder.setAuthor(model.getAuthor());
                    holder.setDescription(model.getDescription());
            }

            @NonNull
            @Override
            public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news, parent, false);
                return new NewsHolder(view);
            }
        };


        return view;
    }
}
