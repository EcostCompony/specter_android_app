package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.Routing;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChannelFragment extends Fragment {

    LinearLayout lToolsMenu, lHeadChannel;
    TextView tChannelTitle, tNumberSubscribers, tEditPost;
    ImageView bSend, bClose;
    Button bSubscribe;
    EditText ePost;
    PostsAdapter posts_adapter;
    RecyclerView recPosts;
    String channelTitle, channelId;
    Integer channelAdmin, editId;
    boolean edit;
    Post edPost;
    List<Post> posts = new ArrayList<>();
    ChannelActivity channelActivity;
    ChildEventListener childEventListenerPosts, childEventListenerSubscribers;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channel, container, false);

        lToolsMenu = inflaterView.findViewById(R.id.tools_menu);
        lHeadChannel = inflaterView.findViewById(R.id.channel_head);
        tChannelTitle = inflaterView.findViewById(R.id.title_channel);
        tNumberSubscribers = inflaterView.findViewById(R.id.number_subscribers);
        tEditPost = inflaterView.findViewById(R.id.edit_post);
        bSend = inflaterView.findViewById(R.id.button_enter);
        bClose = inflaterView.findViewById(R.id.button_close);
        bSubscribe = inflaterView.findViewById(R.id.button_subscribe);
        recPosts = inflaterView.findViewById(R.id.post_rec);
        ePost = inflaterView.findViewById(R.id.input_post);
        channelActivity = (ChannelActivity) requireActivity();
        channelAdmin = channelActivity.channelAdmin;
        channelId = channelActivity.channelId;
        channelTitle = channelActivity.channelTitle;

        channelActivity.subscribers.clear();

        registerForContextMenu(bSend);

        tChannelTitle.setText(channelTitle);
        if (!channelAdmin.equals(authId)) {
            lToolsMenu.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,60);
            recPosts.setLayoutParams(layoutParams);
        }

        PostsAdapter.OnPostLongClickListener postLongClickListener = (post, position) -> {
            CharSequence[] items;
            AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());

            if (channelAdmin.equals(authId)) items = new String[]{getString(R.string.post_edit), getString(R.string.post_copy), getString(R.string.post_delete)};
            else items = new String[]{getString(R.string.post_copy)};

            builder.setItems(items, (dialog, item) -> {
                if (items[item].equals(getString(R.string.post_edit))) {
                    edit = true;
                    edPost = post;
                    editId = position;
                    tEditPost.setVisibility(View.VISIBLE);
                    ePost.setText(post.context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,0,0,40);
                    recPosts.setLayoutParams(layoutParams);
                } else if (items[item].equals(getString(R.string.post_copy))) {
                    ClipboardManager clipboard = (ClipboardManager) inflater.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("post", post.context);
                    clipboard.setPrimaryClip(clip);
                } else if (items[item].equals(getString(R.string.post_delete))) {
                    posts.remove(position);
                    myDB.child("specter").child("channels").child(channelId).child("body").setValue(posts.size() == 0 ? "not posts" : posts.get(posts.size() - 1).context);
                    myDB.child("specter").child("channels").child(channelId).child("posts").setValue(posts.size() == 0 ? null : posts);
                }
            }).create().show();

            return true;
        };
        recPosts.setLayoutManager(new LinearLayoutManager(channelActivity));
        posts_adapter = new PostsAdapter(channelActivity, posts, postLongClickListener);
        recPosts.setAdapter(posts_adapter);

        childEventListenerPosts = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Post post = Objects.requireNonNull(dataSnapshot.getValue(Post.class));
                if (Objects.equals(post.author, "%CHANNEL_TITLE%")) post.author = channelTitle;
                posts.add(post);
                posts_adapter.notifyDataSetChanged();
                recPosts.smoothScrollToPosition(posts.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                posts.set(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())), dataSnapshot.getValue(Post.class));
                posts_adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                posts.remove(dataSnapshot.getKey());
                posts_adapter.notifyDataSetChanged();
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(channelId).child("posts").addChildEventListener(childEventListenerPosts);

        childEventListenerSubscribers = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Integer id = dataSnapshot.getValue(Integer.class);
                channelActivity.subscribers.add(id);
                if (Objects.equals(id, authId)) bSubscribe.setVisibility(View.GONE);
                tNumberSubscribers.setText(Locale.getDefault().getLanguage().equals("ru") ? pluralForm(channelActivity.subscribers.size(), getString(R.string.subscriber1), getString(R.string.subscribers2), getString(R.string.subscribers3)) : pluralForm(channelActivity.subscribers.size(), getString(R.string.subscriber1), getString(R.string.subscribers2)));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                channelActivity.subscribers.remove(dataSnapshot.getKey());
                tNumberSubscribers.setText(Locale.getDefault().getLanguage().equals("ru") ? pluralForm(channelActivity.subscribers.size(), getString(R.string.subscriber1), getString(R.string.subscribers2), getString(R.string.subscribers3)) : pluralForm(channelActivity.subscribers.size(), getString(R.string.subscriber1), getString(R.string.subscribers2)));
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(channelId).child("subscribers").addChildEventListener(childEventListenerSubscribers);

        //lHeadChannel.setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).addToBackStack(null).commit());

        bClose.setOnClickListener(view -> channelActivity.finish());

        bSubscribe.setOnClickListener(view -> myDB.child("specter").child("channels").child(channelId).child("subscribers").child(String.valueOf(channelActivity.subscribers.size())).setValue(authId));

        bSend.setOnClickListener(view -> {
            String text = ePost.getText().toString().trim();
            if (!text.equals("")) {
                if (edit) {
                    edPost.context = text;
                    myDB.child("specter").child("channels").child(channelId).child("posts").child(String.valueOf(editId)).setValue(edPost);
                    if (posts.size()-1 == editId) myDB.child("channels").child(channelId).child("body").setValue(text);
                    tEditPost.setVisibility(View.GONE);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0,0,0,0);
                    recPosts.setLayoutParams(layoutParams);
                    edit = false;
                } else {
                    DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("users/joe/lastOnline");
                    userLastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    userLastOnlineRef.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long unixTime = Objects.requireNonNull(snapshot.getValue(Long.class));
                            String hours = new java.text.SimpleDateFormat("HH").format(new java.util.Date(unixTime));
                            String minutes = new java.text.SimpleDateFormat("mm").format(new java.util.Date(unixTime));
                            if (Integer.parseInt(hours) < 21) hours = String.valueOf(Integer.parseInt(hours) + 3);
                            else hours = String.valueOf(Integer.parseInt(hours) + 3 - 24);

                            myDB.child("specter").child("channels").child(channelId).child("posts").child(String.valueOf(posts.size())).setValue(new Post(authUserName, hours + ":" + minutes, text));
                            myDB.child("specter").child("channels").child(channelId).child("body").setValue(text);
                            myDB.child("specter").child("channels").child(channelId).child("mark_body").setValue(false);
                            recPosts.smoothScrollToPosition(posts.size());
                        }

                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            ePost.setText("");
        });

        return inflaterView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!edit) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(0, v.getId(), 0, R.string.send_anon);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == getString(R.string.send_anon)) {
            String text = ePost.getText().toString().trim();
            if (!text.equals("")) {
                DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("users/joe/lastOnline");
                userLastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                userLastOnlineRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long unixTime = Objects.requireNonNull(snapshot.getValue(Long.class));
                        String hours = new java.text.SimpleDateFormat("HH").format(new java.util.Date(unixTime));
                        String minutes = new java.text.SimpleDateFormat("mm").format(new java.util.Date(unixTime));
                        if (Integer.parseInt(hours) < 21) hours = String.valueOf(Integer.parseInt(hours) + 3);
                        else hours = String.valueOf(Integer.parseInt(hours) + 3 - 24);

                        myDB.child("specter").child("channels").child(channelId).child("posts").child(String.valueOf(posts.size())).setValue(new Post("%CHANNEL_TITLE%", hours+":"+minutes, text));
                        myDB.child("specter").child("channels").child(channelId).child("body").setValue(text);
                        recPosts.smoothScrollToPosition(posts.size());
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            ePost.setText("");
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myDB.child("specter").child("channels").child(channelId).child("posts").removeEventListener(childEventListenerPosts);
        myDB.child("specter").child("channels").child(channelId).child("subscribers").removeEventListener(childEventListenerSubscribers);
    }

}