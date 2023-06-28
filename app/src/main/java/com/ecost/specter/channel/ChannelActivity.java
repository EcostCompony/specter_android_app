package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ChannelActivity extends AppCompatActivity {

    public Integer channelId, channelCategory, channelSubscriberNumbers;
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
        channelCategory = getIntent().getIntExtra("CHANNEL_CATEGORY", 0);
        channelDescription = getIntent().getStringExtra("CHANNEL_DESCRIPTION");
        channelSubscriberNumbers = getIntent().getIntExtra("CHANNEL_SUBSCRIBER_NUMBERS", 0);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://95.163.236.254:3501/api/method/channels.getById?v=0.7&channel_id=" + channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) finish();
                    else {
                        userSubscribe = response.getChannel().getIsSubscriber() == 1;
                        userAdmin = response.getChannel().getIsAdmin() == 1;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit();
                    }
                });
            }
        });
    }

}