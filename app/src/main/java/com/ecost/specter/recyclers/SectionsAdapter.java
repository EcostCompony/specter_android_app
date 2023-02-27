package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    public interface OnSectionClickListener {
        void onSectionClick(int position);
    }

    SectionsAdapter.OnSectionClickListener onClickListener;
    List<String> names;
    Integer sectionPosition;
    LayoutInflater inflater;

    public SectionsAdapter(Context context, List<String> names, Integer sectionPosition, SectionsAdapter.OnSectionClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.names = names;
        this.sectionPosition = sectionPosition;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (sectionPosition == position) return R.layout.section_mark_item;
        return R.layout.section_item;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SectionViewHolder(inflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        holder.itemView.setOnClickListener(v -> onClickListener.onSectionClick(position));
    }

    @Override
    public int getItemCount() {
        if (names != null) return names.size();
        else return 0;
    }

}