package com.racoders.racodersproject.classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.racoders.racodersproject.R;

import org.w3c.dom.Text;

class NewsHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final  TextView author;
    private final TextView description;

    public NewsHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        author = itemView.findViewById(R.id.author);
        description = itemView.findViewById(R.id.description);

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

}
