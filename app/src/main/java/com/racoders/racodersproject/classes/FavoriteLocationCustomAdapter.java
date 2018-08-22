package com.racoders.racodersproject.classes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.PublicLocationProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FavoriteLocationCustomAdapter extends RecyclerView.Adapter<FavoriteLocationCustomAdapter.ViewHolder> {

    private List<PointOfInterest> mList;

    public FavoriteLocationCustomAdapter(List<PointOfInterest> list){
        mList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView locationImage;
        private final TextView locationName;
        private final TextView locationAddress;
        private final TextView locationFollowers;
        private final TextView locationPosts;
        private final RelativeLayout layout;
        private int sum = 0;

        public ViewHolder(View itemView) {
            super(itemView);

            locationImage = itemView.findViewById(R.id.locationImage);
            locationName = itemView.findViewById(R.id.locationName);
            locationAddress = itemView.findViewById(R.id.locationAddress);
            locationFollowers = itemView.findViewById(R.id.locationFollowers);
            locationPosts = itemView.findViewById(R.id.locationPostsNumber);
            layout = itemView.findViewById(R.id.layout);

        }

        public void setLocationImage(String key){
            String[] getKey = key.split("/");
            String myKey = getKey[getKey.length-1];
            StorageReference storage = FirebaseStorage.getInstance().getReference().child("images/pois/" + myKey + ".jpeg");
            GlideApp.with(getApplicationContext()).load(storage).into(locationImage);
        }

        public void setLocationName(String name){
            locationName.setText(name);
        }

        public void setLocationAddress(String address){
            locationAddress.setText(address);
        }

        public void setLocationPosts(final String key){
            String[] getKey = key.split("/");
            String myKey = getKey[getKey.length-1];
            FirebaseDatabase.getInstance().getReference().child("News").child(myKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String postsString = dataSnapshot.getChildrenCount() + " posts";
                        locationPosts.setText(postsString);
                    }else{
                        locationPosts.setText("0 posts");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setLocationFollowers(final String key){
            FirebaseDatabase.getInstance().getReference().child("FavoriteLocations")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot children : dataSnapshot.getChildren()){
                                    GenericTypeIndicator<ArrayList<String> > t = new GenericTypeIndicator<ArrayList<String> >(){};
                                    ArrayList<String> usersFav = children.getValue(t);

                                    for(String str : usersFav)
                                        if(str.equals(key))
                                            sum++;
                                }

                                String followersNumberString =  sum + " followers";
                                Spanned sp = Html.fromHtml(followersNumberString);
                                locationFollowers.setText(sp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    @NonNull
    @Override
    public FavoriteLocationCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_location, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteLocationCustomAdapter.ViewHolder holder, final int position) {
        holder.setLocationName(mList.get(position).getTitle());
        holder.setLocationAddress(mList.get(position).getAdress());
        holder.setLocationFollowers(mList.get(position).getKey());
        holder.setLocationPosts(mList.get(position).getKey());
        holder.setLocationImage(mList.get(position).getKey());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationId = getLocationId(mList.get(position).getKey());
                getApplicationContext().startActivity(new Intent(getApplicationContext(), PublicLocationProfile.class).putExtra("id", locationId));
            }
        });
    }

    private String getLocationId(String key) {
        String[] getKey = key.split("/");

        return getKey[getKey.length-1];
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
