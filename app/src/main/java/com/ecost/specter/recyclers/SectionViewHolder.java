package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class SectionViewHolder  extends RecyclerView.ViewHolder {

    FrameLayout flItem;
    TextView tvName;

    public SectionViewHolder(View itemView) {
        super(itemView);

        flItem = itemView.findViewById(R.id.item);
        tvName = itemView.findViewById(R.id.name);
    }

}