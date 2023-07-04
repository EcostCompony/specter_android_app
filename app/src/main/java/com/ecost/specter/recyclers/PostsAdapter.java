package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.accessToken;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.Response;
import com.ecost.specter.api.SpecterAPI;
import com.ecost.specter.models.Post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder> {

    public interface OnPostLongClickListener {
        boolean onPostLongClick(Post post, int position, View view);
    }

    private final OnPostLongClickListener onLongClickListener;
    private final String channelTitle;
    private final List<Post> posts;
        private final LayoutInflater inflater;
        private Response response;

    public PostsAdapter(Context context, String channelTitle, List<Post> posts, PostsAdapter.OnPostLongClickListener onLongClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
        this.channelTitle = channelTitle;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(inflater.inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("users.getById", "&user_id=" + post.getAuthorId(), accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) holder.tvAuthor.setText(channelTitle);
                    else holder.tvAuthor.setText(response.getUser().getName());
                    holder.tvText.setText(post.getText());
                    holder.tvTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new java.util.Date(post.getDatetime())));
                    holder.itemView.setOnLongClickListener(v -> onLongClickListener.onPostLongClick(post, position, holder.itemView));
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (posts != null) return posts.size();
        else return 0;
    }

}