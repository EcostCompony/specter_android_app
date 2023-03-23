package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Appeal;
import com.ecost.specter.models.Channel;

import java.util.List;

public class AppealsAdapter extends RecyclerView.Adapter<AppealViewHolder> {

    public interface OnAppealClickListener {
        void onAppealClick(Appeal appeal, int position);
    }

    OnAppealClickListener onClickListener;
    List<Appeal> appeals;
    LayoutInflater inflater;

    public AppealsAdapter(Context context, List<Appeal> appeals, OnAppealClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.appeals = appeals;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public AppealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.channel_item, parent, false);
        return new AppealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppealViewHolder holder, int position) {
        Appeal appeal = appeals.get(position);
        holder.tTopic.setText(appeal.topic);
        holder.tLastPost.setText(appeal.body);
        holder.itemView.setOnClickListener(v -> onClickListener.onAppealClick(appeal, position));
    }

    @Override
    public int getItemCount() {
        if (appeals != null) return appeals.size();
        else return 0;
    }

}