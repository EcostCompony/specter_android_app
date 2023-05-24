/*package com.ecost.specter.channel;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;
import com.ecost.specter.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Objects;

public class ChannelActivity extends AppCompatActivity {

    Integer channelId, channelCategoryId;
    String channelTitle, shortChannelLink, channelDescription, userSubscriberId;
    boolean userSubscribe, userAdmin;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        shortChannelLink = getIntent().getStringExtra("CHANNEL_SHORT_LINK");
        channelCategoryId = getIntent().getIntExtra("CHANNEL_CATEGORY", 0);
        channelDescription = getIntent().getStringExtra("CHANNEL_DESCRIPTION");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                User user = Objects.requireNonNull(dataSnapshot.getValue(User.class));
                if (user.getId().equals(authId)) userSubscriberId = dataSnapshot.getKey();
                if (user.getId().equals(authId)) userSubscribe = true;
                if (user.getId().equals(authId) && user.getChannelAdmin()) userAdmin = true;
            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("channels").child(String.valueOf(channelId)).child("subscribers").addChildEventListener(childEventListener);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
            myDB.child("specter").child("channels").child(String.valueOf(channelId)).child("subscribers").removeEventListener(childEventListener);
        }, 100);
    }

}*/