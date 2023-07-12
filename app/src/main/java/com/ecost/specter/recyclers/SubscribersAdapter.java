package com.ecost.specter.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Subscriber;

import java.util.List;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscriberViewHolder> {

    public interface OnSetAdminClickListener {
        void onSetAdminClick(int position);
    }

    SubscribersAdapter.OnSetAdminClickListener onClickListener;
    List<Subscriber> subscribers;
    Context context;
    LayoutInflater inflater;

    public SubscribersAdapter(Context context, List<Subscriber> subscribers, SubscribersAdapter.OnSetAdminClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.subscribers = subscribers;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SubscriberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubscriberViewHolder(inflater.inflate(R.layout.subscriber_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubscriberViewHolder holder, int position) {
        Subscriber subscriber = subscribers.get(position);
        holder.tvName.setText(subscriber.getUser().getName());
        holder.tvShortLink.setText(subscriber.getUser().getShortLink());
        if (subscribers.get(position).getIsAdmin() == 0) holder.ibSetAdmin.setVisibility(View.VISIBLE);
        holder.ibSetAdmin.setOnClickListener(view -> {
            onClickListener.onSetAdminClick(position);
            holder.ibSetAdmin.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        if (subscribers != null) return subscribers.size();
        else return 0;
    }

}