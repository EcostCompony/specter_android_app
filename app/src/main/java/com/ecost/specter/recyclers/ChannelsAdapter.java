package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;

import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    OnChannelClickListener onClickListener;
    List<Channel> channels;
    LayoutInflater inflater;

    public ChannelsAdapter(Context context, List<Channel> channels, OnChannelClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.channels = channels;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.channel_item, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        Channel channel = channels.get(position);
        holder.tvTitle.setText(channel.getTitle());
        holder.itemView.setOnClickListener(v -> onClickListener.onChannelClick(channel));
    }

    @Override
    public int getItemCount() {
        if (channels != null) return channels.size();
        else return 0;
    }

}