package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.authId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    public interface OnPostLongClickListener {
        boolean onPostLongClick(Post post, int position);
    }

    OnPostLongClickListener onLongClickListener;
    List<Post> posts;
    LayoutInflater inflater;

    public PostsAdapter(Context context, List<Post> posts, PostsAdapter.OnPostLongClickListener onLongClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).senderId == authId) return R.layout.my_comment_item;
        return R.layout.post_item;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(inflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);
        holder.text.setText(post.context);
        if (holder.author != null) holder.author.setText(post.author);
        holder.time.setText(post.time);
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onPostLongClick(post, position));
    }

    @Override
    public int getItemCount() {
        if (posts != null) return posts.size();
        else return 0;
    }

}