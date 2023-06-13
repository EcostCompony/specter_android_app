package com.ecost.specter.menu;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.API;
import com.ecost.specter.api.Response;
import com.ecost.specter.models.Channel;
import com.ecost.specter.recyclers.ChannelsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class ChannelsMenuFragment extends Fragment {

    private MainMenuActivity mainMenuActivity;
    private ChannelsAdapter channelsAdapter;
    private Response response;
    private final List<Channel> channels = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();

        channels.clear();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://thespecterlife.com:3501/api/method/channels.get?v=1.0", accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(mainMenuActivity, super.getView(), 2, getString(R.string.unknown_error));
                    else {
                        Channel[] channels1 = response.getChannelsRes();
                        for (Channel channel : channels1) if (channel.getBody().equals("%CHANNEL_CREATED%")) channel.setBody(getString(R.string.channels_menu_channel_body_channel_created));
                        channels.addAll(Arrays.asList(channels1));
                        channelsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_menu, container, false);

        RecyclerView rvChannelsList = inflaterView.findViewById(R.id.recycler_channels_list);
        mainMenuActivity = (MainMenuActivity) requireActivity();

        rvChannelsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, mainMenuActivity::openChannel);
        rvChannelsList.setAdapter(channelsAdapter);

        inflaterView.findViewById(R.id.hitbox_button_search).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsSearchFragment()).addToBackStack(null).commit());
        inflaterView.findViewById(R.id.hitbox_button_navigate).setOnClickListener(view -> new NavigationFragment().show(mainMenuActivity.getSupportFragmentManager(), new NavigationFragment().getTag()));

        return inflaterView;
    }

}