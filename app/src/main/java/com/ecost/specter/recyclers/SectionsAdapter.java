package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.sectionPosition;

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
    LayoutInflater inflater;

    List<String> names;

    public SectionsAdapter(Context context, List<String> names, SectionsAdapter.OnSectionClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.names = names;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SectionViewHolder(inflater.inflate(R.layout.section_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.tvName.setText(names.get(position));
        if (sectionPosition == position) holder.flItem.setAlpha(1);
        else holder.flItem.setAlpha(0.5F);
        holder.itemView.setOnClickListener(v -> onClickListener.onSectionClick(position));
    }

    @Override
    public int getItemCount() {
        if (names != null) return names.size();
        else return 0;
    }

}