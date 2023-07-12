package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.Response;
import com.ecost.specter.api.SpecterAPI;
import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    OnChannelClickListener onClickListener;
    List<Channel> channels;
    LayoutInflater inflater;
    private Context context;
    private Response response;

    public ChannelsAdapter(Context context, List<Channel> channels, OnChannelClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.channels = channels;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChannelViewHolder(inflater.inflate(R.layout.channel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        Channel channel = channels.get(position);
        if (channel.getInactive() != null) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            if (channel.getBody() == null) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        response = new SpecterAPI("posts.get", "&channel_id=" + channel.getId() + "&count=1", accessToken).call();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (response.getError() == null) {
                                Post[] posts = response.getList().getPosts();
                                holder.tvBody.setText(posts.length == 1 ? posts[0].getText() : context.getString(R.string.channels_menu_channel_body_channel_created));
                            }
                        });
                    }
                });
            }
            holder.tvTitle.setText(channel.getTitle());
            if (channel.getBody() != null) holder.tvBody.setText(channel.getBody());
            holder.itemView.setOnClickListener(v -> onClickListener.onChannelClick(channel));
        }
    }

    @Override
    public int getItemCount() {
        if (channels != null) return channels.size();
        else return 0;
    }

}