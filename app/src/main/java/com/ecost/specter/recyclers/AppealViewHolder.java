package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class AppealViewHolder extends RecyclerView.ViewHolder {

    TextView tTopic, tLastPost;

    public AppealViewHolder(View itemView) {
        super(itemView);
        tTopic = itemView.findViewById(R.id.appeal_topic);
        tLastPost = itemView.findViewById(R.id.appeal_last_post);
    }

}