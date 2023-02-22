package com.ecost.specter.channel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;

public class ChannelActivity extends AppCompatActivity {

    Integer channelId, channelAdmin, postId, postsNumber, categoryId;
    Integer channelSubscribers = 0;
    String channelTitle, channelShortLink, channelDescription;
    boolean userSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);
        channelAdmin = getIntent().getIntExtra("CHANNEL_ADMINS", 0);
        postsNumber = getIntent().getIntExtra("CHANNEL_POSTS_NUMBER", 0);
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        channelShortLink = getIntent().getStringExtra("CHANNEL_SHORT_LINK");
        categoryId = getIntent().getIntExtra("CHANNEL_CATEGORY", 0);
        channelDescription = getIntent().getStringExtra("CHANNEL_DESCRIPTION");
        userSubscribe = getIntent().getBooleanExtra("USER_SUBSCRIBE", false);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
    }

}