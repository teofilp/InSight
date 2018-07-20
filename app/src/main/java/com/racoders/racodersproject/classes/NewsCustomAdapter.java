package com.racoders.racodersproject.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewsCustomAdapter extends RecyclerView.Adapter<NewsCustomAdapter.ViewHolder> {

    ArrayList<News> mList;
    News news;

    public NewsCustomAdapter(ArrayList<News> mList){
        this.mList = mList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final  TextView author;
        private final TextView description;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.imageView);

        }

        public void setTitle(String text){
            title.setText(text);
        }

        public void setAuthor(String text){
            author.setText(text);
        }

        public void setDescription(String text){
            description.setText(text);
        }
        public void setImageView(String id, Context context){
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/" + id + ".jpeg");
            GlideApp.with(getApplicationContext()).load(storage).into(imageView);
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTitle(mList.get(position).getTitle());
        holder.setAuthor(mList.get(position).getAuthor());
        holder.setDescription(mList.get(position).getDescription());
        holder.setImageView(mList.get(position).getId(), getApplicationContext());

        final String Uid = mList.get(position).getId();
        System.out.println(Uid);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
