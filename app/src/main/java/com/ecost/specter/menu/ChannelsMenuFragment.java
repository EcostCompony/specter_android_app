package com.ecost.specter.menu;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

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

    Response response;
    List<Channel> channels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_menu, container, false);

        RecyclerView rvChannelsList = inflaterView.findViewById(R.id.recycler_channels_list);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        rvChannelsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        ChannelsAdapter channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, mainMenuActivity::openChannel);
        rvChannelsList.setAdapter(channelsAdapter);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new API("http://213.219.214.94:3501/api/method/channels.get?v=1.0", accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(mainMenuActivity, inflaterView, 2, getString(R.string.unknown_error));
                    else {
                        channels.addAll(Arrays.asList(response.getRes().getChannels()));
                        channelsAdapter.notifyItemMoved(0, channels.size());
                    }
                });
            }
        });

        // inflaterView.findViewById(R.id.button_navigate).setOnClickListener(view -> new NavigationFragment().show(mainMenuActivity.getSupportFragmentManager(), new NavigationFragment().getTag()));
        // inflaterView.findViewById(R.id.button_search).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelsSearchFragment()).addToBackStack(null).commit());

        return inflaterView;
    }

}