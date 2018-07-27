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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;
import com.racoders.racodersproject.classes.NewsCustomAdapter;

import java.util.ArrayList;

public class newsFeed extends Fragment {

    private View view;
    private RecyclerView recyclerView;


    public newsFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_feed, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final ArrayList<News> mArrayList = new ArrayList<>();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot child : ds.getChildren()){
                        News news = child.getValue(News.class);
                        mArrayList.add(news);
                    }
                }
                System.out.println("arr size: " + mArrayList.size());
                NewsCustomAdapter adapter = new NewsCustomAdapter(mArrayList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("array size: "+ mArrayList.size());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();





//        Query query = FirebaseDatabase.getInstance().getReference().child("News");

//        FirebaseRecyclerOptions<News> options = new FirebaseRecyclerOptions.Builder<News>()
//                .setQuery(query, News.class)
//                .build();
//        FirebaseRecyclerAdapter<News, NewsHolder> adapter = new FirebaseRecyclerAdapter<News, NewsHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull NewsHolder holder, int position, @NonNull News model) {
//                holder.setTitle(model.getTitle());
//                holder.setAuthor(model.getAuthor());
//                holder.setDescription(model.getDescription());
//
//                final String Uid = getRef(position).getKey();
//                System.out.println(Uid);
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.news, parent, false);
//                return new NewsHolder(view);
//            }
//        };
//        adapter.startListening();
//        recyclerView.setAdapter(adapter);

    }
}
