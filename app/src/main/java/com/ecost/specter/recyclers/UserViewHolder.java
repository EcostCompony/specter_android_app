package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    TextView name, shortLink;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        shortLink = itemView.findViewById(R.id.user_short_link);
    }

}