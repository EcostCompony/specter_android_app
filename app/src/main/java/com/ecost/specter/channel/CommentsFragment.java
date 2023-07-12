package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showPopupMenu;
import static com.ecost.specter.Routing.showToastMessage;
import static com.ecost.specter.Routing.userId;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.Response;
import com.ecost.specter.api.SpecterAPI;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class CommentsFragment extends Fragment {

    private RecyclerView rvCommentsList;
    private ChannelActivity channelActivity;
    private PostsAdapter postsAdapter;
    private final List<Post> comments = new ArrayList<>();
    private Response response;
    private Post commentEditable;
    private int postId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_comments, container, false);

        rvCommentsList = inflaterView.findViewById(R.id.recycler_comments_list);
        TextView tvEditingComment = inflaterView.findViewById(R.id.editing_mode);
        EditText etComment = inflaterView.findViewById(R.id.input_comment);
        channelActivity = (ChannelActivity) requireActivity();

        assert getArguments() != null;
        postId = getArguments().getInt("POST_ID");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(channelActivity);
        linearLayoutManager.setReverseLayout(true);
        rvCommentsList.setLayoutManager(linearLayoutManager);
        postsAdapter = new PostsAdapter(channelActivity, channelActivity.channelTitle, comments, (comment, position, view) -> {
            PopupMenu popupMenu = showPopupMenu(channelActivity, view, R.menu.popup_menu_comment, item -> {
                if (item.getItemId() == R.id.edit) {
                    commentEditable = comment;
                    tvEditingComment.setVisibility(View.VISIBLE);
                    etComment.setText(comment.getText());
                    etComment.setSelection(comment.getText().length());
                    etComment.requestFocus();
                    etComment.postDelayed(() -> ((InputMethodManager) channelActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT), 100);
                } else if (item.getItemId() == R.id.copy) ((ClipboardManager) channelActivity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("comment", comment.getText()));
                else if (item.getItemId() == R.id.delete) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            response = new SpecterAPI("comments.delete", "&channel_id=" + channelActivity.channelId + "&post_id=" + postId + "&comment_id=" + comment.getId(), accessToken).call();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                                else showComments(view);
                            });
                        }
                    });
                }
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            if (!channelActivity.userAdmin && comment.getAuthor().getId() != userId) {
                popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popupMenu.show();
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return false;
        });
        rvCommentsList.setAdapter(postsAdapter);
        showComments(inflaterView);

        inflaterView.findViewById(R.id.hitbox_button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().popBackStackImmediate());

        inflaterView.findViewById(R.id.hitbox_button_send).setOnClickListener(view -> {
            String text = etComment.getText().toString().trim();

            if (text.equals("")) return;
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    response = new SpecterAPI(commentEditable == null ? "comments.create" : "comments.edit", commentEditable == null ? ("&channel_id=" + channelActivity.channelId + "&post_id=" + postId + "&text=" + text) : ("&channel_id=" + channelActivity.channelId + "&post_id=" + postId + "&comment_id=" + commentEditable.getId() + "&text=" + text), accessToken).call();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                        else if (commentEditable == null) {
                            comments.add(0, response.getPost());
                            postsAdapter.notifyItemInserted(0);
                            rvCommentsList.scrollToPosition(comments.size() - 1);
                            etComment.setText("");
                        } else {
                            showComments(view);
                            tvEditingComment.setVisibility(View.GONE);
                            etComment.setText("");
                            commentEditable = null;
                        }
                    });
                }
            });
        });

        return inflaterView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showComments(View view) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("comments.get", "&channel_id=" + channelActivity.channelId + "&post_id=" + postId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        comments.clear();
                        comments.addAll(Arrays.asList(response.getList().getPosts()));
                        postsAdapter.notifyDataSetChanged();
                        rvCommentsList.scrollToPosition(comments.size() - 1);
                    }
                });
            }
        });
    }

}