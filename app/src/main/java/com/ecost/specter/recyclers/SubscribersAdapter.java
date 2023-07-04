package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.accessToken;

import android.annotation.SuppressLint;
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
import com.ecost.specter.models.Subscriber;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscriberViewHolder> {

    public interface OnSetAdminClickListener {
        void onSetAdminClick(int position);
    }

    SubscribersAdapter.OnSetAdminClickListener onClickListener;
    List<Subscriber> subscribers;
    Context context;
    LayoutInflater inflater;
    private Response response;

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
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("users.getById", "&user_id=" + subscriber.getUserId(), accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) {
                        holder.tvName.setText(null);
                        holder.tvShortLink.setText(null);
                    } else {
                        holder.tvName.setText(response.getUser().getName());
                        holder.tvShortLink.setText(response.getUser().getShortLink());
                    }
                    if (subscribers.get(position).getIsAdmin() == 0) holder.ibSetAdmin.setVisibility(View.VISIBLE);
                    holder.ibSetAdmin.setOnClickListener(view -> {
                        onClickListener.onSetAdminClick(position);
                        holder.ibSetAdmin.setVisibility(View.GONE);
                    });
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (subscribers != null) return subscribers.size();
        else return 0;
    }

}