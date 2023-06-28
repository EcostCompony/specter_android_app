package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.pluralForm;
import static com.ecost.specter.Routing.showPopupMenu;
import static com.ecost.specter.Routing.showToastMessage;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class ChannelFragment extends Fragment {

    private RecyclerView rvPostsList;
    private ChannelActivity channelActivity;
    private PostsAdapter postsAdapter;
    private final List<Post> posts = new ArrayList<>();
    private Response response;
    private Post postEditable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel, container, false);

        Button bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        rvPostsList = inflaterView.findViewById(R.id.recycler_posts_list);
        TextView tvEditingPost = inflaterView.findViewById(R.id.editing_mode);
        EditText etBroadcast = inflaterView.findViewById(R.id.input_post);
        FrameLayout flSend = inflaterView.findViewById(R.id.hitbox_button_send);
        channelActivity = (ChannelActivity) requireActivity();

        ((TextView) inflaterView.findViewById(R.id.title)).setText(channelActivity.channelTitle);
        ((TextView) inflaterView.findViewById(R.id.number_subscribers)).setText(pluralForm(channelActivity.channelSubscriberNumbers, getResources().getStringArray(R.array.subscribers)));
        if (!channelActivity.userSubscribe) bSubscribe.setVisibility(View.VISIBLE);
        if (channelActivity.userAdmin) inflaterView.findViewById(R.id.admin_panel).setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(channelActivity);
        linearLayoutManager.setStackFromEnd(true);
        rvPostsList.setLayoutManager(linearLayoutManager);
        postsAdapter = new PostsAdapter(channelActivity, posts, (post, position, view) -> {
            PopupMenu popupMenu = showPopupMenu(channelActivity, view, R.menu.popup_menu_post, item -> {
                if (item.getItemId() == R.id.comments) {
                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("POST_ID", post.getId());
                    commentsFragment.setArguments(bundle);
                    channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, commentsFragment).addToBackStack(null).commit();
                } else if (item.getItemId() == R.id.edit) {
                    postEditable = post;
                    tvEditingPost.setVisibility(View.VISIBLE);
                    etBroadcast.setText(post.getText());
                    etBroadcast.setSelection(post.getText().length());
                    etBroadcast.requestFocus();
                    etBroadcast.postDelayed(() -> ((InputMethodManager) channelActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etBroadcast, InputMethodManager.SHOW_IMPLICIT), 100);
                } else if (item.getItemId() == R.id.copy) ((ClipboardManager) channelActivity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("post", post.getText()));
                else if (item.getItemId() == R.id.delete) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            response = new API("http://95.163.236.254:3501/api/method/posts.delete?v=0.7&channel_id=" + channelActivity.channelId + "&post_id=" + post.getId(), accessToken).call();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                                else showPosts(view);
                            });
                        }
                    });
                }
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            if (!channelActivity.userAdmin) {
                popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popupMenu.show();
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return false;
        });
        rvPostsList.setAdapter(postsAdapter);
        showPosts(inflaterView);

        inflaterView.findViewById(R.id.channel_header).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).addToBackStack(null).commit());

        bSubscribe.setOnClickListener(view -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3501/api/method/channels.subscribe?v=0.7&channel_id=" + channelActivity.channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                    else {
                        channelActivity.userSubscribe = true;
                        bSubscribe.setVisibility(View.GONE);
                    }
                });
            }
        }));

        inflaterView.findViewById(R.id.hitbox_button_close).setOnClickListener(view -> channelActivity.finish());

        flSend.setOnClickListener(view -> {
            String text = etBroadcast.getText().toString().trim();

            if (text.equals("")) return;
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    response = new API(postEditable == null ? ("http://95.163.236.254:3501/api/method/posts.create?v=0.7&channel_id=" + channelActivity.channelId + "&text=" + text + "&author=1") : ("http://95.163.236.254:3501/api/method/posts.edit?v=0.7&channel_id=" + channelActivity.channelId + "&post_id=" + postEditable.getId() + "&text=" + text), accessToken).call();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                        else if (postEditable == null) {
                            posts.add(response.getPost());
                            postsAdapter.notifyItemInserted(posts.size() - 1);
                            rvPostsList.scrollToPosition(posts.size() - 1);
                            etBroadcast.setText("");
                        } else {
                            showPosts(view);
                            tvEditingPost.setVisibility(View.GONE);
                            etBroadcast.setText("");
                            postEditable = null;
                        }
                    });
                }
            });
        });

        flSend.setOnLongClickListener(view -> {
            showPopupMenu(channelActivity, view, R.menu.popup_menu_send_post, item -> {
                String text = etBroadcast.getText().toString().trim();

                if (text.equals("") || postEditable != null) return false;
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        response = new API("http://95.163.236.254:3501/api/method/posts.create?v=0.7&channel_id=" + channelActivity.channelId + "&text=" + text + "&author=2", accessToken).call();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                            else {
                                posts.add(response.getPost());
                                postsAdapter.notifyItemInserted(posts.size() - 1);
                                rvPostsList.scrollToPosition(posts.size() - 1);
                                etBroadcast.setText("");
                            }
                        });
                    }
                });
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return false;
        });

        return inflaterView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showPosts(View view) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3501/api/method/posts.get?v=0.7&channel_id=" + channelActivity.channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        posts.clear();
                        posts.addAll(Arrays.asList(response.getPosts()));
                        postsAdapter.notifyDataSetChanged();
                        rvPostsList.scrollToPosition(posts.size() - 1);
                    }
                });
            }
        });
    }

}