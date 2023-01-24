package com.ecost.specter.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

public class ChapterViewHolder extends RecyclerView.ViewHolder {

    TextView tChapterName;

    public ChapterViewHolder(View itemView) {
        super(itemView);
        tChapterName = itemView.findViewById(R.id.chapter_name);
    }

}