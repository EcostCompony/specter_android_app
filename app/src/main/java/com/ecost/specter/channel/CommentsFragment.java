package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popupMenu;
import static com.ecost.specter.Routing.translateData;

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

import com.ecost.specter.DataTimeTask;
import com.ecost.specter.R;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CommentsFragment extends Fragment {

    Post commentEditable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_comments, container, false);

        RecyclerView rvCommentsList = inflaterView.findViewById(R.id.recycler_comments_list);
        TextView tvEditComment = inflaterView.findViewById(R.id.edit_comment);
        EditText etComment = inflaterView.findViewById(R.id.input_comment);
        List<Post> comments = new ArrayList<>();
        assert getArguments() != null;
        int postId = getArguments().getInt("POST_ID");
        ChannelActivity channelActivity = (ChannelActivity) requireActivity();

        rvCommentsList.setLayoutManager(new LinearLayoutManager(channelActivity));
        PostsAdapter commentsAdapter = new PostsAdapter(channelActivity, comments, (comment, position, view) -> {
            popupMenu(channelActivity, view, R.menu.popupmenu_comment, item -> {
                if (item.getItemId() == R.id.edit) {
                    commentEditable = comment;
                    tvEditComment.setVisibility(View.VISIBLE);
                    etComment.setText(comment.getContext());
                } else if (item.getItemId() == R.id.copy) ((ClipboardManager) channelActivity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("comment", comment.getContext()));
                else if (item.getItemId() == R.id.delete) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postId)).child("comments").child(String.valueOf(comment.getId())).setValue(null);
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return true;
        });
        rvCommentsList.setAdapter(commentsAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post comment = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                Long unix1 = comments.size() == 0 ? comment.getDate() : comments.get(comments.size()-1).getDate();
                Long unix2 = comment.getDate();
                if (comments.size() == 0 || !translateData(unix1, "yyyy").equals(translateData(unix2, "yyyy")) || !translateData(unix1, "yyyy").equals(translateData(unix2, "MM")) || !translateData(unix1, "yyyy").equals(translateData(unix2, "dd"))) comment.setType(1);
                comments.add(comment);
                commentsAdapter.notifyItemInserted(comments.size()-1);
                rvCommentsList.post(() -> rvCommentsList.scrollToPosition(commentsAdapter.getItemCount()-1));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                for (int i = 0; i < comments.size(); i++) {
                    if (comments.get(i).getId() == Objects.requireNonNull(dataSnapshot.getValue(Post.class)).getId()) {
                        comments.set(i, Objects.requireNonNull(dataSnapshot.getValue(Post.class)));
                        commentsAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < comments.size(); i++) {
                    if (comments.get(i).getId() == Objects.requireNonNull(dataSnapshot.getValue(Post.class)).getId()) {
                        comments.remove(i);
                        commentsAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postId)).child("comments").addChildEventListener(childEventListener);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        inflaterView.findViewById(R.id.button_send).setOnClickListener(view -> {
            String text = etComment.getText().toString().trim();
            if (text.equals("")) return;
            if (commentEditable != null) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postId)).child("comments").child(String.valueOf(commentEditable.getId())).child("context").setValue(text);
            if (commentEditable == null)
                try {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postId)).child("comments").child(String.valueOf(comments.size() == 0 ? 0 : comments.get(comments.size()-1).getId()+1)).setValue(new Post(comments.size() == 0 ? 0 : comments.get(comments.size()-1).getId()+1, authId, authUserName, new DataTimeTask().execute("somestring").get(), text));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            tvEditComment.setVisibility(View.GONE);
            commentEditable = null;
            etComment.setText("");
        });

        return inflaterView;
    }

}