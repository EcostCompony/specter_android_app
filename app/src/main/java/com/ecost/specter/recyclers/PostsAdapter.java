package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    public interface OnPostLongClickListener {
        boolean onPostLongClick(Post post, int position, View view);
    }

    OnPostLongClickListener onLongClickListener;
    List<Post> posts;
    LayoutInflater inflater;

    public PostsAdapter(Context context, List<Post> posts, PostsAdapter.OnPostLongClickListener onLongClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(inflater.inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.tvText.setText(post.getText());
        holder.tvAuthor.setText(post.getAuthor() == null ? post.getAuthorName() : post.getAuthor());
        holder.tvTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new java.util.Date(post.getDatetime())));
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onPostLongClick(post, position, holder.itemView));
    }

    @Override
    public int getItemCount() {
        if (posts != null) return posts.size();
        else return 0;
    }

}