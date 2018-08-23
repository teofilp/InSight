package com.racoders.racodersproject.classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.NewsActivity;
import com.racoders.racodersproject.activities.PublicLocationProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewsCustomAdapter extends RecyclerView.Adapter<NewsCustomAdapter.ViewHolder> {

    private final List<News> mList;
    private final int color;

    public NewsCustomAdapter(List<News> mList, int color){
        this.mList = mList;
        Collections.reverse(mList);
        this.color = color;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final  TextView author;
        private final ImageView authorImageView;
        private TextView dateTextView;
        private final RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            authorImageView = itemView.findViewById(R.id.authorImageview);
            layout = itemView.findViewById(R.id.layout);

        }

        public void setTitle(String text){
            title.setText(text);
        }

        public void setAuthor(final String text){
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("POIs");
            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot childs : dataSnapshot.getChildren())
                            for(DataSnapshot childsOfChilds : childs.getChildren())
                                if(childsOfChilds.getKey().equals(text))
                                    author.setText(childsOfChilds.getValue(PointOfInterest.class).getTitle());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        public void setDateTextView(Date date){
            String outputDate;
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy", new Locale("en_US"));
            outputDate = formatter.format(date);
            this.dateTextView.setText(outputDate);
        }


        public void setLayoutTag(int tag) { layout.setTag(tag);}

        public Object getLayoutTag() { return layout.getTag();}


        public void setAuthorImage(String id){
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + id + ".jpeg");
            GlideApp.with(getApplicationContext()).load(storage).into(authorImageView);
        }

        public void setLayoutColor(){
            layout.setBackgroundColor(color);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setTitle(mList.get(position).getTitle());
        holder.setAuthor(mList.get(position).getAuthor());
        holder.setAuthorImage(mList.get(position).getAuthor());
        holder.setLayoutTag(position);
        holder.setDateTextView(mList.get(position).getPublicationDate());
        holder.setLayoutColor();
        final String Uid = mList.get(position).getId();
        System.out.println(Uid);


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().startActivity(new Intent(getApplicationContext(), NewsActivity.class)
                    .putExtra("reference", mList.get((int)v.getTag()).getReference()).putExtra("position", (int)v.getTag()));
            }
        });

        holder.authorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getApplicationContext()
                        .startActivity(new Intent(getApplicationContext(), PublicLocationProfile.class)
                        .putExtra("id", mList.get(position).getAuthor()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<News> getmList() {
        return mList;
    }
}
