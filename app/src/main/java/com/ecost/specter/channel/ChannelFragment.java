package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authEcostId;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authShortUserLink;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;
import static com.ecost.specter.Routing.popupMenu;
import static com.ecost.specter.Routing.translateData;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.DataTimeTask;
import com.ecost.specter.R;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.User;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ChannelFragment extends Fragment {

    ChildEventListener childEventListenerPosts, childEventListenerSubscribers;
    List<Post> posts = new ArrayList<>();
    Post postEditable;
    int channelSubscribers = 0;
    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel, container, false);

        Button bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        RecyclerView rvPostsList = inflaterView.findViewById(R.id.recycler_posts_list);
        TextView tvEditingPost = inflaterView.findViewById(R.id.edit_post);
        EditText etPost = inflaterView.findViewById(R.id.input_post);
        LinearLayout bSendPost = inflaterView.findViewById(R.id.button_send);
        channelActivity = (ChannelActivity) requireActivity();

        ((TextView) inflaterView.findViewById(R.id.channel_title)).setText(channelActivity.channelTitle);
        if (!channelActivity.userSubscribe) bSubscribe.setVisibility(View.VISIBLE);
        if (channelActivity.userAdmin) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvPostsList.getLayoutParams();
            params.topMargin = Math.round(65 * (channelActivity.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            rvPostsList.setLayoutParams(params);
            inflaterView.findViewById(R.id.tools_menu).setVisibility(View.VISIBLE);
        }

        rvPostsList.setLayoutManager(new LinearLayoutManager(channelActivity));
        PostsAdapter postsAdapter = new PostsAdapter(channelActivity, posts, (post, position, view) -> {
            popupMenu(channelActivity, view, R.menu.popupmenu_post, item -> {
                if (item.getItemId() == R.id.comments) {
                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("POST_ID", post.id);
                    commentsFragment.setArguments(bundle);
                    channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, commentsFragment).addToBackStack(null).commit();
                } else if (item.getItemId() == R.id.edit) {
                    postEditable = post;
                    tvEditingPost.setVisibility(View.VISIBLE);
                    etPost.setText(post.context);
                } else if (item.getItemId() == R.id.copy) ((ClipboardManager) channelActivity.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("post", post.context));
                else if (item.getItemId() == R.id.delete) {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(posts.size() - 1 == 0 ? "%NOT_POSTS%" : (posts.get(posts.size() - 1).id == post.id ? posts.get(posts.size() - 2).context : posts.get(posts.size() - 1).context));
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(posts.size() - 1 == 0);
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(post.id)).setValue(null);
                }
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return false;
        });
        rvPostsList.setAdapter(postsAdapter);

        childEventListenerSubscribers = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                channelSubscribers++;
                ((TextView) inflaterView.findViewById(R.id.number_subscribers)).setText(pluralForm(channelSubscribers, getString(R.string.number_subscribers_nominative_case), getString(R.string.number_subscribers_genitive_case), getString(R.string.number_subscribers_plural_genitive_case), Locale.getDefault().getLanguage().equals("ru")));
            }

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").addChildEventListener(childEventListenerSubscribers);

        childEventListenerPosts = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post post = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                if (post.author.equals("%CHANNEL_TITLE%")) post.author = channelActivity.channelTitle;
                Long unix1 = posts.size() == 0 ? post.date : posts.get(posts.size()-1).date;
                Long unix2 = post.date;
                if (posts.size() == 0 || !translateData(unix1, "yyyy").equals(translateData(unix2, "yyyy")) || !translateData(unix1, "MM").equals(translateData(unix2, "MM")) || !translateData(unix1, "dd").equals(translateData(unix2, "dd"))) post.type = 1;
                posts.add(post);
                postsAdapter.notifyItemInserted(posts.size()-1);
                rvPostsList.post(() -> rvPostsList.scrollToPosition(postsAdapter.getItemCount()-1));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).id == Objects.requireNonNull(dataSnapshot.getValue(Post.class)).id) {
                        posts.set(i, Objects.requireNonNull(dataSnapshot.getValue(Post.class)));
                        postsAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).id == Objects.requireNonNull(dataSnapshot.getValue(Post.class)).id) {
                        posts.remove(i);
                        postsAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").addChildEventListener(childEventListenerPosts);

        inflaterView.findViewById(R.id.channel_header).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).addToBackStack(null).commit());

        bSubscribe.setOnClickListener(view -> {
            myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").push().setValue(new User(authId, authEcostId, authUserName, authShortUserLink));
            channelActivity.userSubscribe = true;
            bSubscribe.setVisibility(View.GONE);
        });

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.finish());

        bSendPost.setOnClickListener(view -> {
            String text = etPost.getText().toString().trim();
            if (text.equals("")) return;
            if (postEditable != null) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(postEditable.id)).child("context").setValue(text);
            if (postEditable == null || posts.size() == postEditable.id) myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(text);
            if (postEditable == null) {
                try {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(posts.size() == 0 ? 0 : posts.get(posts.size()-1).id+1)).setValue(new Post(posts.size() == 0 ? 0 : posts.get(posts.size()-1).id+1, authUserName, new DataTimeTask().execute("somestring").get(), text));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(false);
            }
            tvEditingPost.setVisibility(View.GONE);
            postEditable = null;
            etPost.setText("");
        });

        bSendPost.setOnLongClickListener(view -> {
            popupMenu(channelActivity, view, R.menu.popupmenu, item -> {
                String text = etPost.getText().toString().trim();
                if (text.equals("") || postEditable != null) return false;
                try {
                    myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").child(String.valueOf(posts.size() == 0 ? 0 : posts.get(posts.size()-1).id+1)).setValue(new Post(posts.size() == 0 ? 0 : posts.get(posts.size()-1).id+1, "%CHANNEL_TITLE%", new DataTimeTask().execute("somestring").get(), text));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("body").setValue(text);
                myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("markBody").setValue(false);
                etPost.setText("");
                return true;
            }, menu -> inflaterView.findViewById(R.id.dim_layout).setVisibility(View.INVISIBLE));
            inflaterView.findViewById(R.id.dim_layout).setVisibility(View.VISIBLE);
            return false;
        });

        return inflaterView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        posts.clear();
        channelSubscribers = 0;
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("posts").removeEventListener(childEventListenerPosts);
        myDB.child("specter").child("channels").child(String.valueOf(channelActivity.channelId)).child("subscribers").removeEventListener(childEventListenerSubscribers);
    }

}