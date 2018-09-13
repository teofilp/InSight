package com.racoders.racodersproject.classes;

import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.racoders.racodersproject.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsCustomAdapter extends RecyclerView.Adapter<ReviewsCustomAdapter.ViewHolder> {

    private List<Review> mList;

    public ReviewsCustomAdapter(List<Review> list){
        mList = list;
        Collections.reverse(mList);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView author;
        private final TextView description;
        private final TextView date;
        private final RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.reviewAuthor);
            description = itemView.findViewById(R.id.reviewDescription);
            date = itemView.findViewById(R.id.reviewDate);
            rating = itemView.findViewById(R.id.reviewRating);
        }
        public void setAuthor(String value){

            FirebaseDatabase.getInstance().getReference().child("Users").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        author.setText(dataSnapshot.getValue(User.class).getDisplayName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setDescription(String description){
            this.description.setText(description);
        }

        public void setDate(Date date){

            String outputDate;
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy", new Locale("en_US"));
            outputDate = formatter.format(date);
            this.date.setText(outputDate);
        }

        public void setRating(double value){
            rating.setRating((float)value);
        }

    }
    @NonNull
    @Override
    public ReviewsCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsCustomAdapter.ViewHolder holder, int position) {
        holder.setAuthor(mList.get(position).getAuthor());
        holder.setDescription(mList.get(position).getDescription());
        holder.setDate(mList.get(position).getDate());
        holder.setRating(mList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
