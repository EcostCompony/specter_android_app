package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.models.Channel;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChannelFragment extends Fragment {

    LinearLayout lChannelHead, bClose, bSendPost;
    TextView tChannelTitle, tNumberSubscribers, tEditPost;
    Button bSubscribe;
    RecyclerView rPostsList;
    FrameLayout fToolsMenu;
    EditText ePost;
    ChildEventListener childEventListenerPosts, childEventListenerSub;
    PostsAdapter postsAdapter;
    Post postEdit;
    List<Post> posts = new ArrayList<>();
    ChannelActivity channelActivity;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel, container, false);

        lChannelHead = inflaterView.findViewById(R.id.channel_header);
        tChannelTitle = inflaterView.findViewById(R.id.channel_title);
        tNumberSubscribers = inflaterView.findViewById(R.id.number_subscribers);
        bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        bClose = inflaterView.findViewById(R.id.button_close);
        rPostsList = inflaterView.findViewById(R.id.recycler_posts_list);
        fToolsMenu = inflaterView.findViewById(R.id.tools_menu);
        tEditPost = inflaterView.findViewById(R.id.edit_post);
        ePost = inflaterView.findViewById(R.id.input_post);
        bSendPost = inflaterView.findViewById(R.id.button_send);
        channelActivity = (ChannelActivity) requireActivity();

        channelActivity.channelSubscribers = 0;
        registerForContextMenu(bSendPost);
        tChannelTitle.setText(channelActivity.channelTitle);
        if (!channelActivity.userSubscribe) bSubscribe.setVisibility(View.VISIBLE);
        if (channelActivity.channelAdmin.equals(authId)) {
            DisplayMetrics displayMetrics = channelActivity.getResources().getDisplayMetrics();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rPostsList.getLayoutParams();
            params.topMargin = Math.round(65 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            rPostsList.setLayoutParams(params);
            fToolsMenu.setVisibility(View.VISIBLE);
        }

        PostsAdapter.OnPostLongClickListener postLongClickListener = (post, position) -> {
            CharSequence[] items = channelActivity.channelAdmin.equals(authId) ? new String[]{getString(R.string.channel_alert_dialog_item_comments), getString(R.string.channel_alert_dialog_item_edit), getString(R.string.channel_alert_dialog_item_copy), getString(R.string.channel_alert_dialog_item_delete)} : new String[]{getString(R.string.channel_alert_dialog_item_comments), getString(R.string.channel_alert_dialog_item_copy)};
            AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());

            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getString(R.string.channel_alert_dialog_item_comments))) {
                    channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new CommentsFragment()).addToBackStack(null).commit();
                    channelActivity.postId = post.id;
                } else if (items[item].equals(getString(R.string.channel_alert_dialog_item_edit))) {
                    postEdit = post;
                    tEditPost.setVisibility(View.VISIBLE);
                    ePost.setText(post.context);
                } else if (items[item].equals(getString(R.string.channel_alert_dialog_item_copy))) ((ClipboardManager) inflater.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("post", post.context));
                else if (items[item].equals(getString(R.string.channel_alert_dialog_item_delete))) {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(posts.size() - 1 == 0 ? "%NOT_POSTS%" : (posts.get(posts.size() - 1).id == post.id ? posts.get(posts.size() - 2).context : posts.get(posts.size() - 1).context));
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(posts.size() - 1 == 0);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(post.id)).setValue(null);
                }
            }).create().show();

            return true;
        };
        rPostsList.setLayoutManager(new LinearLayoutManager(channelActivity));
        postsAdapter = new PostsAdapter(channelActivity, posts, postLongClickListener);
        rPostsList.setAdapter(postsAdapter);

        childEventListenerPosts = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post post = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                if (Objects.equals(post.author, "%CHANNEL_TITLE%")) post.author = channelActivity.channelTitle;
                posts.add(post);
                postsAdapter.notifyDataSetChanged();
                rPostsList.smoothScrollToPosition(posts.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post post = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                for (int i = 0; i < postsAdapter.getItemCount(); i++) {
                    if (posts.get(i).id == post.id) {
                        posts.set(i, post);
                        postsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Post post = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).id == post.id) {
                        posts.remove(i);
                        postsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").addChildEventListener(childEventListenerPosts);

        childEventListenerSub = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                channelActivity.channelSubscribers++;
                tNumberSubscribers.setText(pluralForm(channelActivity.channelSubscribers, getString(R.string.number_subscribers_nominative_case), getString(R.string.number_subscribers_genitive_case), getString(R.string.number_subscribers_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                channelActivity.channelSubscribers--;
                tNumberSubscribers.setText(pluralForm(channelActivity.channelSubscribers, getString(R.string.number_subscribers_nominative_case), getString(R.string.number_subscribers_genitive_case), getString(R.string.number_subscribers_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListenerSub);

        bClose.setOnClickListener(view -> channelActivity.finish());

        lChannelHead.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).addToBackStack(null).commit());

        bSubscribe.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").child(String.valueOf(channelActivity.channelSubscribers)).setValue(authId);
            channelActivity.channelSubscribers++;
            channelActivity.userSubscribe = true;
            bSubscribe.setVisibility(View.GONE);
            tNumberSubscribers.setText(pluralForm(channelActivity.channelSubscribers, getString(R.string.number_subscribers_nominative_case), getString(R.string.number_subscribers_genitive_case), getString(R.string.number_subscribers_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
        });

        bSendPost.setOnClickListener(view -> {
            String text = ePost.getText().toString().trim();
            if (!text.equals("")) {
                if (postEdit != null) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postEdit.id)).child("context").setValue(text);
                if (postEdit == null || channelActivity.postsNumber == postEdit.id) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(text);
                if (postEdit == null) {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(channelActivity.postsNumber)).setValue(new Post(channelActivity.postsNumber, authUserName, "15:23", text));
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("postsNumber").setValue(channelActivity.postsNumber + 1);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(false);
                    channelActivity.postsNumber++;
                    rPostsList.smoothScrollToPosition(posts.size());
                }
                tEditPost.setVisibility(View.GONE);
                postEdit = null;
            }
            ePost.setText("");
        });

        return inflaterView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (postEdit == null) menu.add(0, view.getId(), 0, R.string.channel_context_menu_item_send_without_name);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String text = ePost.getText().toString().trim();
        if (!text.equals("")) {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(posts.size())).setValue(new Post(posts.size(), "%CHANNEL_TITLE%", "15:23", text));
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(text);
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(false);
            rPostsList.smoothScrollToPosition(posts.size());
        }
        ePost.setText("");
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        posts.clear();
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").removeEventListener(childEventListenerPosts);
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListenerSub);
    }

}