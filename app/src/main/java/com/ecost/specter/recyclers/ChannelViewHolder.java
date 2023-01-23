package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    TextView tTitle, tLastPost;

    public ChannelViewHolder(View itemView) {
        super(itemView);
        tTitle = itemView.findViewById(R.id.channel_title);
        tLastPost = itemView.findViewById(R.id.channel_last_post);
    }
}
