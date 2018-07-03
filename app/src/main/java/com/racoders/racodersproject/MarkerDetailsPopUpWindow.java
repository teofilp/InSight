package com.racoders.racodersproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

class MarkerDetailsPopUpWindow extends Activity{

    private TextView title;
    private TextView description;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_details_pop_up_window);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        String id = getIntent().getStringExtra("id");



    }
}
