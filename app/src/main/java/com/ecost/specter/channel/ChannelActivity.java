package com.ecost.specter.channel;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;

import java.util.ArrayList;
import java.util.List;

public class ChannelActivity extends AppCompatActivity {

    String channelId, channelTitle, channelLink;
    Integer channelAdmin;
    List<Integer> subscribers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        channelId = getIntent().getStringExtra("CHANNEL_ID");
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        channelLink = getIntent().getStringExtra("CHANNEL_LINK");
        channelAdmin = getIntent().getIntExtra("CHANNEL_ADMINS", 0);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
    }

    public void startChannelPageFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelPageFragment()).addToBackStack(null).commit();
    }

    public void startChannelSettingsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelSettingsFragment()).addToBackStack(null).commit();
    }

}