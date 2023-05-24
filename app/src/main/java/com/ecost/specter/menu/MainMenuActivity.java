/* package com.ecost.specter.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;
import com.ecost.specter.channel.ChannelActivity;
import com.ecost.specter.models.Channel;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (getIntent().getBooleanExtra("CREATE", false)) {
            getIntent().putExtra("CREATE", false);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsMenuFragment()).commit();
        }
    }

    public void startChannel(Channel channel) {
        Intent intent = new Intent(this, ChannelActivity.class);
        intent.putExtra("CHANNEL_ID", channel.getId());
        intent.putExtra("CHANNEL_TITLE", channel.getTitle());
        intent.putExtra("CHANNEL_SHORT_LINK", String.valueOf(channel.getShortLink()));
        intent.putExtra("CHANNEL_CATEGORY", channel.getCategoryId());
        intent.putExtra("CHANNEL_DESCRIPTION", channel.getDescription());
        startActivity(intent);
    }

} */