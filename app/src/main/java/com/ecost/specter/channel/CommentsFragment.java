package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsFragment extends Fragment {

    RecyclerView rCommentsList;
    TextView tEditComment;
    EditText eComment;
    LinearLayout bSendComment;
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
        bSendComment = inflaterView.findViewById(R.id.button_send);
        channelActivity = (ChannelActivity) requireActivity();

        PostsAdapter.OnPostLongClickListener postLongClickListener = (post, position) -> true;
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

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").addChildEventListener(childEventListener);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit());

        bSendComment.setOnClickListener(view -> {
            String text = eComment.getText().toString().trim();
            if (!text.equals("")) {
                if (commentEdit != null) commentEdit.context = text;
                if (commentEdit == null) rCommentsList.smoothScrollToPosition(comments.size());
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postId)).child("comments").child(String.valueOf(commentEdit != null ? commentEdit.id : comments.size())).setValue(commentEdit != null ? commentEdit : new Post(comments.size(), authUserName, "15:23", text));
                tEditComment.setVisibility(View.GONE);
                commentEdit = null;
            }
            eComment.setText("");
        });

        return inflaterView;
    }

}