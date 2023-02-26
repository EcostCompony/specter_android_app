package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;

import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    List<String> names;
    LayoutInflater inflater;

    public SectionsAdapter(Context context, List<String> names) {
        this.inflater = LayoutInflater.from(context);
        this.names = names;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SectionViewHolder(inflater.inflate(R.layout.section_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        holder.name.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        if (names != null) return names.size();
        else return 0;
    }

}