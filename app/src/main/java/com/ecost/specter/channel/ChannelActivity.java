package com.ecost.specter.channel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    Integer channelId, channelAdmin;
    String channelTitle, channelLink;
    List<Integer> subscribers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);
        channelAdmin = getIntent().getIntExtra("CHANNEL_ADMINS", 0);
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        channelLink = getIntent().getStringExtra("CHANNEL_LINK");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
    }

}