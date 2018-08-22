package com.racoders.racodersproject.classes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.racoders.racodersproject.R;
import com.racoders.racodersproject.activities.LocationWithSpecificCategory;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<Category> {

    private Context context;
    private List<Category> categories;

    public CategoryListAdapter(Context context, List<Category> categories){
        super(context, R.layout.category_element_layout, categories);
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.category_element_layout, parent, false);

        final Category category = categories.get(position);

        ImageView imageView = view.findViewById(R.id.categoryImage);
        TextView textView = view.findViewById(R.id.categoryTitle);
        LinearLayout layout = view.findViewById(R.id.layout);

        imageView.setBackground(getContext().getResources().getDrawable(category.getImage()));
        textView.setText(category.getName());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext().getApplicationContext(), LocationWithSpecificCategory.class).putExtra("categoryName", category.getName()));
            }
        });


        return view;
    }
}
