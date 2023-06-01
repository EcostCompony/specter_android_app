package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class SubscriberViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvShortLink;
    ImageButton ibSetAdmin;

    public SubscriberViewHolder(View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.name);
        tvShortLink = itemView.findViewById(R.id.short_link);
        ibSetAdmin = itemView.findViewById(R.id.button_set_admin);
    }

}