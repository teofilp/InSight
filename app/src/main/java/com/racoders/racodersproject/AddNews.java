package com.racoders.racodersproject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class AddNews extends AppCompatActivity {

    private EditText title;
    private EditText author;
    private EditText description;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        description = findViewById(R.id.description);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                News mNews = new News(title.getText().toString(), author.getText().toString(),
                        description.getText().toString(), Calendar.getInstance().getTime(), "");

                DatabaseReference mDbref = FirebaseDatabase.getInstance().getReference().child("News");

                String mId = mDbref.push().getKey();
                mNews.setId(mId);
                System.out.println(mId);
                mDbref.child(mId).setValue(mNews, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null){
                            Toast.makeText(AddNews.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("News").child("-LHlNuiOejVYkHWrp4Jp");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                News news = dataSnapshot.getValue(News.class);
                System.out.println(news.getAuthor() + " " + news.getDescription() + " " + news.getTitle() + " " + news.getPublicationDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
