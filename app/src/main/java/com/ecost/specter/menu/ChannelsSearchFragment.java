package com.ecost.specter.menu;

import static com.ecost.specter.Routing.accessToken;
import static com.ecost.specter.Routing.showToastMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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

public class ChannelsSearchFragment extends Fragment {

    private Response response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_channels_search, container, false);

        EditText etSearchString = inflaterView.findViewById(R.id.input_search_string);
        RecyclerView rvChannelsList = inflaterView.findViewById(R.id.recycler_channels_list);
        MainMenuActivity mainMenuActivity = (MainMenuActivity) requireActivity();

        List<Channel> channels = new ArrayList<>();

        etSearchString.requestFocus();
        etSearchString.postDelayed(() -> ((InputMethodManager) mainMenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etSearchString, InputMethodManager.SHOW_IMPLICIT), 1);

        rvChannelsList.setLayoutManager(new LinearLayoutManager(mainMenuActivity));
        ChannelsAdapter channelsAdapter = new ChannelsAdapter(mainMenuActivity, channels, mainMenuActivity::openChannel);
        rvChannelsList.setAdapter(channelsAdapter);

        etSearchString.addTextChangedListener(new TextWatcher() {
            @SuppressLint("NotifyDataSetChanged")
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = etSearchString.getText().toString().trim();
                channels.clear();

                if (!searchString.equals("")) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            response = new API("http://213.219.214.94:3501/api/method/channels.search?v=1.0&q=" + searchString, accessToken).call();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (response.getError() != null) showToastMessage(mainMenuActivity, inflaterView, 2, getString(R.string.unknown_error));
                                else {
                                    for (Channel channel : response.getRes().getChannels()) channel.setBody(getString(R.string.symbol_at) + channel.getShortLink());
                                    channels.addAll(Arrays.asList(response.getRes().getChannels()));
                                    channelsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                } else channelsAdapter.notifyDataSetChanged();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        inflaterView.findViewById(R.id.hitbox_button_close).setOnClickListener(view -> mainMenuActivity.getSupportFragmentManager().popBackStackImmediate());

        return inflaterView;
    }

}