package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsFragment extends Fragment {

    RecyclerView rCommentsList;
    TextView tEditComment;
    EditText eComment;
    PostsAdapter postsAdapter;
    List<Post> comments = new ArrayList<>();
    Post commentEdit;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_comments, container, false);

        rCommentsList = inflaterView.findViewById(R.id.recycler_comments_list);
        tEditComment = inflaterView.findViewById(R.id.edit_comment);
        eComment = inflaterView.findViewById(R.id.input_comment);
        channelActivity = (ChannelActivity) requireActivity();

        PostsAdapter.OnPostLongClickListener postLongClickListener = (comment, position) -> {
            CharSequence[] items = comment.senderId == authId ? new String[]{getString(R.string.comments_alert_dialog_item_edit), getString(R.string.comments_alert_dialog_item_copy), getString(R.string.comments_alert_dialog_item_delete)} : new String[]{getString(R.string.comments_alert_dialog_item_copy)};
            AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());

            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getString(R.string.channel_alert_dialog_item_edit))) {
                    commentEdit = comment;
                    tEditComment.setVisibility(View.VISIBLE);
                    eComment.setText(comment.context);
                } else if (items[item].equals(getString(R.string.channel_alert_dialog_item_copy))) ((ClipboardManager) inflater.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("comment", comment.context));
                else if (items[item].equals(getString(R.string.channel_alert_dialog_item_delete))) {
                    comments.remove(comment.id);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").setValue(comments.size() == 0 ? null : comments);
                    for (int i = 0; i < comments.size(); i++) {
                        comments.get(i).id = i;
                        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").child(String.valueOf(i)).setValue(comments.get(i));
                    }
                }
            }).create().show();

            return true;
        };
        rCommentsList.setLayoutManager(new LinearLayoutManager(channelActivity));
        postsAdapter = new PostsAdapter(channelActivity, comments, postLongClickListener);
        rCommentsList.setAdapter(postsAdapter);

        @SuppressLint("NotifyDataSetChanged")
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post comment = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                if (Objects.equals(comment.author, "%CHANNEL_TITLE%")) comment.author = channelActivity.channelTitle;
                comments.add(comment);
                postsAdapter.notifyDataSetChanged();
                rCommentsList.smoothScrollToPosition(comments.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                comments.set(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())), dataSnapshot.getValue(Post.class));
                postsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue(Post.class)).senderId != authId) comments.remove(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())));
                postsAdapter.notifyDataSetChanged();
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").addChildEventListener(childEventListener);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit());

        inflaterView.findViewById(R.id.button_send).setOnClickListener(view -> {
            String text = eComment.getText().toString().trim();
            if (!text.equals("")) {
                if (commentEdit != null) commentEdit.context = text;
                if (commentEdit == null) rCommentsList.smoothScrollToPosition(comments.size());
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").child(String.valueOf(commentEdit != null ? commentEdit.id : comments.size())).setValue(commentEdit != null ? commentEdit : new Post(comments.size(), authId, authUserName, "15:23", text));
                tEditComment.setVisibility(View.GONE);
                commentEdit = null;
            }
            eComment.setText("");
        });

        return inflaterView;
    }

}