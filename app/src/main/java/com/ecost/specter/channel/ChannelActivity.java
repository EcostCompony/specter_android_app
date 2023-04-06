package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;
import com.ecost.specter.models.Post;
import com.ecost.specter.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Objects;

public class ChannelActivity extends AppCompatActivity {

    Integer channelId, postId, postsNumber, categoryId;
    Integer settingPosition = 0;
    Integer channelSubscribers = 0;
    String channelTitle, channelShortLink, channelDescription;
    boolean userSubscribe, channelAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);
        postsNumber = getIntent().getIntExtra("CHANNEL_POSTS_NUMBER", 0);
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        channelShortLink = getIntent().getStringExtra("CHANNEL_SHORT_LINK");
        categoryId = getIntent().getIntExtra("CHANNEL_CATEGORY", 0);
        channelDescription = getIntent().getStringExtra("CHANNEL_DESCRIPTION");
        userSubscribe = getIntent().getBooleanExtra("USER_SUBSCRIBE", false);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                if (Objects.requireNonNull(dataSnapshot.getValue(User.class)).id.equals(authId) && Objects.requireNonNull(dataSnapshot.getValue(User.class)).channel_admin) channelAdmin = true;
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelId)).child("subscribers").addChildEventListener(childEventListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
    }

}