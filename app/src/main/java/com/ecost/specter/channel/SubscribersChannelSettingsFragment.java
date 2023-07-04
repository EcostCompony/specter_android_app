package com.ecost.specter.channel;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.api.EcostAPI;
import com.ecost.specter.api.Response;
import com.ecost.specter.api.SpecterAPI;
import com.ecost.specter.models.Subscriber;
import com.ecost.specter.recyclers.SubscribersAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class SubscribersChannelSettingsFragment extends Fragment {

    private ChannelActivity channelActivity;
    private SubscribersAdapter subscribersAdapter;
    private Response response;
    private final List<Subscriber> subscribers = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_subscribers_channel_settings, container, false);

        EditText etSearchString = inflaterView.findViewById(R.id.input_search_string);
        RecyclerView rvSubscribersList = inflaterView.findViewById(R.id.recycler_subscribers_list);
        channelActivity = (ChannelActivity) requireActivity();

        rvSubscribersList.setLayoutManager(new LinearLayoutManager(channelActivity));
        subscribersAdapter = new SubscribersAdapter(channelActivity, subscribers, position -> Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("subscribers.setAdmin", "&channel_id=" + channelActivity.channelId + "&user_id=" + subscribers.get(position).getUserId(), accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        rvSubscribersList.setAdapter(subscribersAdapter);
        showSubscribers(inflaterView);

        etSearchString.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = etSearchString.getText().toString().trim();

                if (!searchString.equals("")) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            response = new SpecterAPI("subscribers.search", "&channel_id=" + channelActivity.channelId + "&q=" + searchString, accessToken).call();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (response.getError() != null) showToastMessage(channelActivity, inflaterView, 2, getString(R.string.unknown_error));
                                else {
                                    subscribers.clear();
                                    subscribers.addAll(Arrays.asList(response.getSubscribers()));
                                    subscribersAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } else showSubscribers(inflaterView);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        return inflaterView;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showSubscribers(View view) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                response = new SpecterAPI("subscribers.get", "&channel_id=" + channelActivity.channelId, accessToken).call();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getError() != null) showToastMessage(channelActivity, view, 2, getString(R.string.unknown_error));
                    else {
                        subscribers.clear();
                        subscribers.addAll(Arrays.asList(response.getSubscribers()));
                        subscribersAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}