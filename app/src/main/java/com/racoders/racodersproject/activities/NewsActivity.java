package com.racoders.racodersproject.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.racoders.racodersproject.AppGlideModule.GlideApp;
import com.racoders.racodersproject.R;
import com.racoders.racodersproject.classes.News;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NewsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private TextView viewsNumber;
    private TextView description;
    private ImageView newsImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.Toolbar);
        viewsNumber = findViewById(R.id.viewsNumber);
        description = findViewById(R.id.newsDescription);
        title = findViewById(R.id.newsTitle);
        newsImage = findViewById(R.id.newsImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String reference = getIntent().getStringExtra("reference");
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReferenceFromUrl(reference);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    News news = dataSnapshot.getValue(News.class);
                    toolbar.setTitle(news.getAuthor());
                    viewsNumber.setText(news.getViewsNumber()+1 +" views");
                    title.setText(news.getTitle());
                    /**
                     * use hmtl preformatted text
                     */

                    Spanned sp = Html.fromHtml(news.getDescription());
                    description.setText(sp);
                    /**
                     * download and load image from storage into the imageView
                     */
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" +
                            news.getId() + ".jpeg");
                    GlideApp.with(getApplicationContext()).load(storageRef).into(newsImage);

                    news.increaseViewsNumber();
                    DatabaseReference saveViewsDbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(reference)
                            .child("viewsNumber");
                    saveViewsDbRef.setValue(news.getViewsNumber(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                finish();
                            }
                        }
                    });

                }else{
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
