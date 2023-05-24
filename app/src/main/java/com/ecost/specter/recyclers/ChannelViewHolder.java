package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle, tvBody;

    public ChannelViewHolder(View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.title);
        tvBody = itemView.findViewById(R.id.body);
    }

}