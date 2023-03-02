package com.ecost.specter.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.FAQPost;

import java.util.List;

public class FAQPostsAdapter extends RecyclerView.Adapter<FAQPostViewHolder> {

    public interface OnFAQPostLongClickListener {
        boolean onFAQPostLongClick(String context);
    }

    FAQPostsAdapter.OnFAQPostLongClickListener onLongClickListener;
    List<FAQPost> faqPosts;
    LayoutInflater inflater;

    public FAQPostsAdapter(Context context, List<FAQPost> faqPosts, FAQPostsAdapter.OnFAQPostLongClickListener onLongClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.faqPosts = faqPosts;
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (faqPosts.get(position).type == 1) return R.layout.my_comment_item;
        return R.layout.post_item;
    }

    @NonNull
    @Override
    public FAQPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FAQPostViewHolder(inflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FAQPostViewHolder holder, int position) {
        FAQPost faqPost = faqPosts.get(position);
        holder.text.setText(faqPost.context);
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onFAQPostLongClick(faqPost.context));
    }

    @Override
    public int getItemCount() {
        if (faqPosts != null) return faqPosts.size();
        else return 0;
    }

}