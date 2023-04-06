package com.ecost.specter.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

    public interface OnAddAdminClickListener {
        void onAddAdminClick(int position);
    }

    UsersAdapter.OnAddAdminClickListener onClickListener;
    List<User> users;
    Context context;
    LayoutInflater inflater;

    public UsersAdapter(Context context, List<User> users, UsersAdapter.OnAddAdminClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.users = users;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(inflater.inflate(R.layout.user_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText(users.get(position).name);
        holder.shortLink.setText(context.getString(R.string.symbol_at) + users.get(position).link);
        holder.fAddAdmin.setOnClickListener(view -> {
            onClickListener.onAddAdminClick(position);
            holder.fAddAdmin.setVisibility(View.GONE);
        });
        if (users.get(position).channel_admin) holder.fAddAdmin.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (users != null) return users.size();
        else return 0;
    }

}