package com.ecost.specter.recyclers;

import static com.ecost.specter.Routing.authId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).senderId == authId) return R.layout.my_comment_item;
        else if (posts.get(position).type == 1) return R.layout.date_post_item;
        return R.layout.post_item;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(inflater.inflate(viewType, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);
        holder.text.setText(post.context);
        if (post.type == 1) {
            List<String> months = Arrays.asList(" января", " февраля", " марта", " апреля", " мая", " июня", " июля", " августа", " сентября", " октября", " ноября", " декабря");
            Date date = new java.util.Date(post.date * 1000L);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("MM");
            sdf1.setTimeZone(java.util.TimeZone.getTimeZone("GMT+3"));
            sdf2.setTimeZone(java.util.TimeZone.getTimeZone("GMT+3"));
            holder.date.setText(sdf1.format(date) + months.get(Integer.parseInt(sdf2.format(date))-1));
        }
        if (holder.author != null) holder.author.setText(post.author);
        Date date = new java.util.Date(post.date * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+3"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);
        holder.time.setText(formattedDate);
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onPostLongClick(post, position, holder.itemView));
    }

    @Override
    public int getItemCount() {
        if (posts != null) return posts.size();
        else return 0;
    }

}