package com.ecost.specter.recyclers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;

import java.util.List;
import java.util.Objects;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel, int position);
    }

    public interface OnChannelLongClickListener {
        boolean onChannelLongClick(Channel channel, int position);
    }

    OnChannelClickListener onClickListener;
    OnChannelLongClickListener onLongClickListener;
    List<Channel> channels;
    Integer color;
    LayoutInflater inflater;

    public ChannelsAdapter(Context context, List<Channel> channels, OnChannelClickListener onClickListener, OnChannelLongClickListener onLongClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.channels = channels;
        this.color = ContextCompat.getColor(context, R.color.main_color);
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
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
        holder.tTitle.setText(channel.title);
        holder.tLastPost.setText(channel.body);
        if (channel.markBody) holder.tLastPost.setTextColor(color);
        holder.itemView.setOnClickListener(v -> onClickListener.onChannelClick(channel, position));
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onChannelLongClick(channel, position));
    }

    @Override
    public int getItemCount() {
        if (channels != null) return channels.size();
        else return 0;
    }

}
