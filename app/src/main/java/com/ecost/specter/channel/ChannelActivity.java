package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;
import com.ecost.specter.api.Response;
import com.ecost.specter.api.SpecterAPI;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ChannelActivity extends AppCompatActivity {

    public Integer channelId, channelCategory, channelSubscribersCount;
    public String channelTitle, channelShortLink, channelDescription;
    public boolean userSubscribe;
    public boolean userAdmin;
    private Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getIntExtra("CHANNEL_ID", 0);
        channelTitle = getIntent().getStringExtra("CHANNEL_TITLE");
        channelShortLink = getIntent().getStringExtra("CHANNEL_SHORT_LINK");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("channels.getById", "&channel_id=" + channelId + "&fields=category description subscribers_count", accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) finish();
                    else {
                        channelCategory = response.getChannel().getCategory();
                        channelDescription = response.getChannel().getDescription();
                        channelSubscribersCount = response.getChannel().getSubscribersCount();
                        userSubscribe = response.getChannel().getIsSubscriber() == 1;
                        userAdmin = response.getChannel().getIsAdmin() == 1;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
                    }
                });
            }
        });
    }

}