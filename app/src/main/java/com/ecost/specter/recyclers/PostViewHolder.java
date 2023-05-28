package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    TextView tvAuthor, tvTime, tvText;

    public PostViewHolder(View itemView) {
        super(itemView);

        tvAuthor = itemView.findViewById(R.id.author);
        tvTime = itemView.findViewById(R.id.time);
        tvText = itemView.findViewById(R.id.text);
    }

}