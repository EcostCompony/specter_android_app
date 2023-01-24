package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Chapter;

import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<ChapterViewHolder> {

    public interface OnChapterClickListener {
        void onChapterClick(Chapter chapter, int position);
    }

    ChaptersAdapter.OnChapterClickListener onClickListener;
    List<Chapter> chapters;
    LayoutInflater inflater;

    public ChaptersAdapter(Context context, List<Chapter> chapters, ChaptersAdapter.OnChapterClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.chapters = chapters;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chapter_item, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.tChapterName.setText(chapter.chapterName);
        holder.itemView.setOnClickListener(v -> onClickListener.onChapterClick(chapter, position));
    }

    @Override
    public int getItemCount() {
        if (chapters != null) return chapters.size();
        else return 0;
    }

}