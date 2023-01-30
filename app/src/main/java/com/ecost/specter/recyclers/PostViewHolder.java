package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    TextView text, author, time;

    public PostViewHolder(View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.post_text);
        author = itemView.findViewById(R.id.post_author);
        time = itemView.findViewById(R.id.post_time);
    }


}
